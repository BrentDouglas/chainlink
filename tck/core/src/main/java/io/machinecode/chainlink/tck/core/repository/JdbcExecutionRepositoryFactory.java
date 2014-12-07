package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.jdbc.DataSourceLookup;
import io.machinecode.chainlink.repository.jdbc.JdbcExecutionRepository;
import io.machinecode.chainlink.spi.configuration.ExecutionRepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.tck.core.DummyDataSource;
import org.jboss.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JdbcExecutionRepositoryFactory implements ExecutionRepositoryFactory {

    private static final Logger log = Logger.getLogger(JdbcExecutionRepositoryFactory.class);

    private static DataSource dataSource;
    private static String username;
    private static String password;

    static {
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
                connection.prepareStatement("delete from job_instance;").executeUpdate();
                connection.prepareStatement("delete from metric;").executeUpdate();
                connection.prepareStatement("delete from property;").executeUpdate();
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
    public ExecutionRepository produce(final ExecutionRepositoryConfiguration configuration) {
        return JdbcExecutionRepository.create(new DataSourceLookup() {
            @Override
            public DataSource getDataSource() {
                return dataSource;
            }
        }, username, password);
    }
}
