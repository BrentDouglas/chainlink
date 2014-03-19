package io.machinecode.chainlink.tck.cdi;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.inject.cdi.CdiInjector;
import io.machinecode.chainlink.repository.jdbc.DataSourceLookup;
import io.machinecode.chainlink.repository.jdbc.JdbcExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import io.machinecode.chainlink.tck.core.DummyDataSource;
import org.jboss.logging.Logger;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JdbcCdiConfigurationFactory implements ConfigurationFactory {

    private static final Logger log = Logger.getLogger(JdbcCdiConfigurationFactory.class);

    private static Weld weld;
    private static WeldContainer container;
    private static final DataSource dataSource;
    private static String username;
    private static String password;

    static {
        weld = new Weld();
        container = weld.initialize();

        try {
            final String prefix = System.getProperty("database.prefix") + ".";
            dataSource = new DummyDataSource(System.getProperty(prefix + "database.url"), System.getProperty(prefix + "database.driver"));
            Connection connection = null;
            try {
                username = System.getProperty(prefix + "database.user");
                password = System.getProperty(prefix + "database.password", "");
                connection = username == null
                        ? dataSource.getConnection()
                        : dataSource.getConnection(username, password);
                connection.setAutoCommit(false);
                connection.prepareStatement("delete from public.job_instance;").executeUpdate();
                connection.prepareStatement("delete from public.metric;").executeUpdate();
                connection.prepareStatement("delete from public.property;").executeUpdate();
                connection.commit();
            } catch (final Exception e) {
                if (connection != null) {
                    connection.rollback();
                }
                throw new RuntimeException(e);
            } finally {
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (RuntimeException e) {
            log.error("", e);
            throw e;
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Configuration produce() throws Exception {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(JdbcExecutionRepository.create(new DataSourceLookup() {
                    @Override
                    public DataSource getDataSource() {
                        return dataSource;
                    }
                }, username, password))
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setArtifactLoaders(CdiArtifactLoader.inject(container.getBeanManager(), CdiArtifactLoader.class))
                .setInjectors(CdiArtifactLoader.inject(container.getBeanManager(), CdiInjector.class))
                .build();
    }
}
