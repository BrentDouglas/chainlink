package io.machinecode.chainlink.tck.core.loader;

import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class WeldArtifactLoaderFactory implements ArtifactLoaderFactory {

    private static final Weld weld;
    private static final WeldContainer container;

    static {
        weld = new Weld();
        container = weld.initialize();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                weld.shutdown();
            }
        }));
    }

    @Override
    public ArtifactLoader produce(final Dependencies dependencies, final Properties properties) {
        return new CdiArtifactLoader(container.getBeanManager());
    }
}
