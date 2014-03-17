package io.machinecode.chainlink.tck.batch;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.repository.jdbc.DataSourceLookup;
import io.machinecode.chainlink.repository.jdbc.JdbcExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import io.machinecode.chainlink.tck.core.DummyDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JdbcBatchConfigurationFactory implements ConfigurationFactory {

    private static DataSource dataSource;

    static {
        try {
            dataSource = new DummyDataSource(System.getProperty("database.url"));
            Connection connection = null;
            try {
                connection = dataSource.getConnection();
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
            throw e;
        } catch (Exception e) {
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
                }, System.getProperty("database.user"), System.getProperty("database.password")))
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .build();
    }
}
