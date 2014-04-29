package io.machinecode.chainlink.tck.core.repository;

import io.machinecode.chainlink.repository.jpa.EntityManagerLookup;
import io.machinecode.chainlink.repository.jpa.JpaExecutionRepository;
import io.machinecode.chainlink.repository.jpa.ResourceLocalTransactionManagerLookup;
import io.machinecode.chainlink.spi.configuration.RepositoryConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ExecutionRepositoryFactory;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JpaExecutionRepositoryFactory implements ExecutionRepositoryFactory {

    private static EntityManagerFactory factory;

    static {
        factory = Persistence.createEntityManagerFactory("TestPU");
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
            throw new RuntimeException(e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                factory.close();
            }
        }));
    }

    @Override
    public ExecutionRepository produce(final RepositoryConfiguration configuration) {
        return new JpaExecutionRepository(new EntityManagerLookup() {
            @Override
            public EntityManagerFactory getEntityManagerFactory() {
                return factory;
            }
        }, new ResourceLocalTransactionManagerLookup());
    }
}
