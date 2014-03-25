package io.machinecode.chainlink.tck.core.loader;

import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CdiArtifactLoaderFactory implements ArtifactLoaderFactory {

    public static Weld weld;
    public static WeldContainer container;

    static {
        weld = new Weld();
        container = weld.initialize();
    }

    @Override
    public ArtifactLoader produce(final LoaderConfiguration configuration) {
        return CdiArtifactLoader.inject(container.getBeanManager(), CdiArtifactLoader.class);
    }
}
