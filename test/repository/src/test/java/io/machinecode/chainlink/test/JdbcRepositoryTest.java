package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.jdbc.DataSourceLookup;
import io.machinecode.chainlink.repository.jdbc.JdbcExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.DummyDataSource;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import org.junit.After;
import org.junit.Ignore;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
//@Ignore
public class JdbcRepositoryTest extends RepositoryTest {

    private static DataSource dataSource;

    @Override
    protected ExecutionRepository _repository() {
        final String prefix = System.getProperty("database.prefix") + ".";
        dataSource = new DummyDataSource(System.getProperty(prefix + "database.url"), System.getProperty("database.driver"));
        return JdbcExecutionRepository.create(new DataSourceLookup() {
            @Override
            public DataSource getDataSource() {
                return dataSource;
            }
        }, System.getProperty(prefix + "database.user"), System.getProperty(prefix + "database.password", ""));
    }

    @After
    public void after() throws Exception {
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
    }
}
