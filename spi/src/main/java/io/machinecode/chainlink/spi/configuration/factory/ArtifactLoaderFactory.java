package io.machinecode.chainlink.spi.configuration.factory;

import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ArtifactLoaderFactory extends Factory<ArtifactLoader, LoaderConfiguration> {

}
