package io.machinecode.chainlink.tck.cdi;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.transaction.LocalTransactionManager;
import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.inject.cdi.CdiInjector;
import io.machinecode.chainlink.repository.jpa.EntityManagerLookup;
import io.machinecode.chainlink.repository.jpa.JpaExecutionRepository;
import io.machinecode.chainlink.repository.jpa.ResourceLocalTransactionManagerLookup;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JpaCdiConfigurationFactory implements ConfigurationFactory {

    private static Weld weld;
    private static WeldContainer container;

    static {
        weld = new Weld();
        container = weld.initialize();
    }


    @Override
    public Configuration produce() throws Exception {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(new JpaExecutionRepository(new EntityManagerLookup() {
                    @Override
                    public EntityManagerFactory getEntityManagerFactory() {
                        return Persistence.createEntityManagerFactory("TestPU");
                    }
                }, new ResourceLocalTransactionManagerLookup()))
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setArtifactLoaders(CdiArtifactLoader.inject(container.getBeanManager(), CdiArtifactLoader.class))
                .setInjectors(CdiArtifactLoader.inject(container.getBeanManager(), CdiInjector.class))
                .build();
    }
}
