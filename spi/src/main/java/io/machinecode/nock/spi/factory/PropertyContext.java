package io.machinecode.nock.spi.factory;

import io.machinecode.nock.spi.inject.ArtifactReference;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PropertyContext {

    Properties getProperties();

    <T extends ArtifactReference> T getReference(T ref);
}
