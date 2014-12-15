package io.machinecode.chainlink.tck.seam;

import io.machinecode.chainlink.inject.seam.SeamArtifactLoader;
import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;

import javax.servlet.ServletContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class SeamArtifactLoaderFactory implements ArtifactLoaderFactory {

    static {
        final ServletContext context = new MockServletContext();
        ServletLifecycle.beginApplication(context);
        new Initialization(context).create().init();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                ServletLifecycle.endApplication(context);
            }
        }));
    }

    @Override
    public ArtifactLoader produce(final LoaderConfiguration configuration) {
        return SeamArtifactLoader.inject("seamArtifactLoader", SeamArtifactLoader.class);
    }
}
