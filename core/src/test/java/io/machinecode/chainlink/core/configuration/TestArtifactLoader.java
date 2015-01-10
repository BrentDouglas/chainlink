package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestArtifactLoader implements ArtifactLoader {
    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws ArtifactOfWrongTypeException {
        switch (id) {
            case "testArtifactLoader": return item(as, new TestArtifactLoader());
            case "testBatchlet": return item(as, new TestBatchlet());
        }
        return null;
    }

    static  <T> T item(final Class<T> as, final Object that) {
        if (!as.isAssignableFrom(that.getClass())) {
            throw new ArtifactOfWrongTypeException();
        }
        return as.cast(that);
    }
}
