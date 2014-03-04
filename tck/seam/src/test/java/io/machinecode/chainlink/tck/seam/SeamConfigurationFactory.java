package io.machinecode.chainlink.tck.seam;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl.Builder;
import io.machinecode.chainlink.core.local.LocalExecutionRepository;
import io.machinecode.chainlink.core.local.LocalTransactionManager;
import io.machinecode.chainlink.seam.SeamArtifactLoader;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.configuration.ConfigurationFactory;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;

import javax.servlet.ServletContext;
import java.util.concurrent.TimeUnit;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SeamConfigurationFactory implements ConfigurationFactory {

    static {
        final ServletContext context = new MockServletContext();
        ServletLifecycle.beginApplication(context);
        new Initialization(context).create().init();
    }

    @Override
    public Configuration produce() {
        return new Builder()
                .setLoader(Thread.currentThread().getContextClassLoader())
                .setRepository(new LocalExecutionRepository())
                .setTransactionManager(new LocalTransactionManager(180, TimeUnit.SECONDS))
                .setArtifactLoaders(SeamArtifactLoader.inject("seamArtifactLoader", SeamArtifactLoader.class))
                .build();
    }
}
