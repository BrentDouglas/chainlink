package io.machinecode.chainlink.spi.configuration;

import io.machinecode.chainlink.spi.inject.ArtifactLoader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ScopeModel {

    Declaration<ArtifactLoader> getArtifactLoader(final String name);

    JobOperatorModel getJobOperator(final String name);
}
