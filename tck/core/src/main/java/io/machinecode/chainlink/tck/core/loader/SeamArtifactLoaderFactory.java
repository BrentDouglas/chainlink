package io.machinecode.chainlink.tck.core.loader;

import io.machinecode.chainlink.inject.seam.SeamArtifactLoader;
import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;

import javax.servlet.ServletContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SeamArtifactLoaderFactory implements ArtifactLoaderFactory {

    static {
        final ServletContext context = new MockServletContext();
        ServletLifecycle.beginApplication(context);
        new Initialization(context).create().init();
    }

    @Override
    public ArtifactLoader produce(final LoaderConfiguration configuration) {
        return SeamArtifactLoader.inject("seamArtifactLoader", SeamArtifactLoader.class);
    }
}