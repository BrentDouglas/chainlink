package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.jdbc.DataSourceLookup;
import io.machinecode.chainlink.repository.jdbc.JdbcExecutionRepository;
import io.machinecode.chainlink.repository.jpa.EntityManagerLookup;
import io.machinecode.chainlink.repository.jpa.JpaExecutionRepository;
import io.machinecode.chainlink.repository.jpa.ResourceLocalTransactionManagerLookup;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.DummyDataSource;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@Ignore
public class JdbcRepositoryTest extends RepositoryTest {

    private static DataSource dataSource;

    @Override
    protected ExecutionRepository _repository() {
        return JdbcExecutionRepository.create(new DataSourceLookup() {
            @Override
            public DataSource getDataSource() {
                return dataSource;
            }
        }, System.getProperty("database.user"), System.getProperty("database.password"));
    }

    @BeforeClass
    public static void beforeClass() {
        dataSource = new DummyDataSource(System.getProperty("database.url"));
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
