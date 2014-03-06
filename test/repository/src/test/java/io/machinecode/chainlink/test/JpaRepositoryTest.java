package io.machinecode.chainlink.test;

import io.machinecode.chainlink.repository.jpa.EntityManagerLookup;
import io.machinecode.chainlink.repository.jpa.JpaExecutionRepository;
import io.machinecode.chainlink.repository.jpa.ResourceLocalTransactionManagerLookup;
import io.machinecode.chainlink.spi.ExecutionRepository;
import io.machinecode.chainlink.test.core.execution.RepositoryTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JpaRepositoryTest extends RepositoryTest {

    private static EntityManagerFactory factory;

    @Override
    protected ExecutionRepository _repository() {
        return new JpaExecutionRepository(new EntityManagerLookup() {
            @Override
            public EntityManagerFactory getEntityManagerFactory() {
                return factory;
            }
        }, new ResourceLocalTransactionManagerLookup());
    }

    @BeforeClass
    public static void beforeClass() {
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
