package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.jdbc.DataSourceLookup;
import io.machinecode.chainlink.repository.jdbc.JdbcExecutionRepository;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.DummyDataSource;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import org.jboss.logging.Logger;
import org.junit.After;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JdbcRepositoryTest extends RepositoryTest {

    private static final Logger log = Logger.getLogger(JdbcRepositoryTest.class);

    private static volatile DataSource dataSource;

    final String prefix = System.getProperty("database.prefix") + ".";
    final String url = System.getProperty(prefix + "database.url");
    final String driverName = System.getProperty("database.driver");
    final String user = System.getProperty(prefix + "database.user");
    final String password = System.getProperty(prefix + "database.password", "");

    @Override
    protected ExecutionRepository _repository() {
        log.infof("Connection: {\n\turl: '%s'\n\tdriver: '%s'\n\tuser: '%s'\n\tpassword: '%s'\n}", this.url, this.driverName, this.user, this.password);
        if (dataSource == null) {
            dataSource = new DummyDataSource(this.url, this.driverName);
        }
        return JdbcExecutionRepository.create(new DataSourceLookup() {
            @Override
            public DataSource getDataSource() {
                return dataSource;
            }
        }, this.user, this.password);
    }

    @After
    public void after() throws Exception {
        Connection connection = null;
        try {
            if (dataSource == null) {
                dataSource = new DummyDataSource(this.url, this.driverName);
            }
            connection = dataSource.getConnection(this.user, this.password);
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
    }
}
