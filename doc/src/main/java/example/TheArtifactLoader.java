package example;

import io.machinecode.chainlink.spi.inject.ArtifactLoader;

import javax.inject.Named;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Named
public class TheArtifactLoader implements ArtifactLoader {
    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
        //Load bean and inject it here...
        return null;
    }
}
