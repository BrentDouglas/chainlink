package io.machinecode.chainlink.spi.expression;

import io.machinecode.chainlink.spi.inject.ArtifactReference;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface PropertyContext {

    Properties getProperties();

    <T extends ArtifactReference> T getReference(T ref);
}
