package io.machinecode.chainlink.tck.batch;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.repository.jdbc.DataSourceLookup;
import io.machinecode.chainlink.repository.jdbc.JdbcExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import io.machinecode.chainlink.tck.core.DummyDataSource;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JdbcBatchConfigurationFactory implements ConfigurationFactory {

    private static final Logger log = Logger.getLogger(JdbcBatchConfigurationFactory.class);

    private static DataSource dataSource;
    private static String username;
    private static String password;

    static {
        try {
            final String prefix = System.getProperty("database.prefix") + ".";
            dataSource = new DummyDataSource(System.getProperty(prefix + "database.url"), prefix + System.getProperty("database.driver"));
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
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Configuration produce() throws Exception {
        return new Builder()
                .setClassLoader(Thread.currentThread().getContextClassLoader())
                .setExecutionRepository(JdbcExecutionRepository.create(new DataSourceLookup() {
                    @Override
                    public DataSource getDataSource() {
                        return dataSource;
                    }
                }, username, password))
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setExecutorFactoryClass(EventedExecutorFactory.class)
                .setProperty(Constants.EXECUTOR_THREAD_POOL_SIZE, "8")
                .build();
    }
}
