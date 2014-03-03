package io.machinecode.nock.tck.cdi;

import io.machinecode.nock.cdi.CdiArtifactLoader;
import io.machinecode.nock.cdi.CdiInjector;
import io.machinecode.nock.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.nock.core.local.LocalExecutionRepository;
import io.machinecode.nock.core.local.LocalTransactionManager;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.configuration.ConfigurationFactory;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CdiConfigurationFactory implements ConfigurationFactory {

    private static Weld weld;
    private static WeldContainer container;

    static {
        weld = new Weld();
        container = weld.initialize();
    }

    @Override
    public Configuration produce() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(new LocalExecutionRepository())
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setArtifactLoaders(CdiArtifactLoader.inject(container.getBeanManager(), CdiArtifactLoader.class))
                .setInjectors(CdiArtifactLoader.inject(container.getBeanManager(), CdiInjector.class))
                .build();
    }
}
