package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestArtifactLoaderFactory implements ArtifactLoaderFactory {

    public static volatile boolean called = false;

    @Override
    public ArtifactLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
        called = true;
        return new TestArtifactLoader();
    }
}
