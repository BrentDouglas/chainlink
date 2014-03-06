package io.machinecode.chainlink.spi.expression;

import io.machinecode.chainlink.spi.inject.ArtifactReference;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PropertyContext {

    Properties getProperties();

    <T extends ArtifactReference> T getReference(T ref);
}
