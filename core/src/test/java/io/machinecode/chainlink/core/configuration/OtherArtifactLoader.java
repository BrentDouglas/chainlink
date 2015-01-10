package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class OtherArtifactLoader implements ArtifactLoader {
    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws ArtifactOfWrongTypeException {
        switch (id) {
            case "otherBatchlet": return TestArtifactLoader.item(as, new TestBatchlet());
        }
        return null;
    }
}
