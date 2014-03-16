package io.machinecode.chainlink.tck.spring;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.inject.spring.SpringArtifactLoader;
import io.machinecode.chainlink.repository.jpa.EntityManagerLookup;
import io.machinecode.chainlink.repository.jpa.JpaExecutionRepository;
import io.machinecode.chainlink.repository.jpa.ResourceLocalTransactionManagerLookup;
import io.machinecode.chainlink.repository.memory.MemoryExecutionRepository;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import org.junit.BeforeClass;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JpaSpringConfigurationFactory implements ConfigurationFactory {

    private static AbstractApplicationContext context;
    private static EntityManagerFactory factory;

    static {
        context = new ClassPathXmlApplicationContext("beans.xml");
        factory = Persistence.createEntityManagerFactory("TestPU");
        final EntityManager em = factory.createEntityManager();
        final EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("delete from JpaJobInstance").executeUpdate();
            em.createQuery("delete from JpaJobExecution").executeUpdate();
            em.createQuery("delete from JpaStepExecution").executeUpdate();
            em.createQuery("delete from JpaMetric").executeUpdate();
            em.createQuery("delete from JpaProperty").executeUpdate();
            em.flush();
            transaction.commit();
        } catch (final Exception e) {
            transaction.rollback();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Configuration produce() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(new JpaExecutionRepository(new EntityManagerLookup() {
                    @Override
                    public EntityManagerFactory getEntityManagerFactory() {
                        return factory;
                    }
                }, new ResourceLocalTransactionManagerLookup()))
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setArtifactLoaders(context.getBean(SpringArtifactLoader.class))
                .build();
    }
}
