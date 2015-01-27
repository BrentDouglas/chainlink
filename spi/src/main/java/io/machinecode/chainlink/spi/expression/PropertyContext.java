package io.machinecode.chainlink.spi.expression;

import io.machinecode.chainlink.spi.inject.ArtifactReference;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface PropertyContext {

    Properties getProperties();

    ArtifactReference getReference(final ArtifactReference ref);
}
