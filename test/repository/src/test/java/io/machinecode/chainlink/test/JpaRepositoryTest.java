package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.jpa.EntityManagerLookup;
import io.machinecode.chainlink.repository.jpa.JpaExecutionRepository;
import io.machinecode.chainlink.repository.jpa.ResourceLocalTransactionManagerLookup;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.core.repository.RepositoryTest;
import org.jboss.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JpaRepositoryTest extends RepositoryTest {

    private static final Logger log = Logger.getLogger(JpaRepositoryTest.class);

    private static EntityManagerFactory factory;

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getExecutionRepository().setFactory(new ExecutionRepositoryFactory() {
            @Override
            public ExecutionRepository produce(final Dependencies dependencies, final Properties properties) throws Exception {
                return _repository = new JpaExecutionRepository(new EntityManagerLookup() {
                    @Override
                    public EntityManagerFactory getEntityManagerFactory() {
                        return factory;
                    }
                }, new ResourceLocalTransactionManagerLookup());
            }
        });
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        final String prefix = System.getProperty("database.prefix") + ".";
        final String url = System.getProperty(prefix + "database.url");
        final String driverName = System.getProperty("database.driver");
        log.infof("Connection: {\n\turl: '%s'\n\tdriver: '%s'\n}", url, driverName);
        try {
            DriverManager.getDriver(url);
        } catch (final SQLException e) {
            final Driver driver = Driver.class.cast(Class.forName(driverName).newInstance());
            DriverManager.registerDriver(driver);
        }
        factory = Persistence.createEntityManagerFactory("TestPU");
    }

    @AfterClass
    public static void afterClass() {
        factory.close();
    }

    @After
    public void after() throws Exception {
        final EntityManager em = factory.createEntityManager();
        final EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("delete from JpaJobInstance").executeUpdate();
            em.createQuery("delete from JpaMetric").executeUpdate();
            em.createQuery("delete from JpaProperty").executeUpdate();
            em.flush();
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            throw e;
        }
    }
}
