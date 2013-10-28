package io.machinecode.nock.spi.factory;

import io.machinecode.nock.spi.inject.ArtifactReference;
import io.machinecode.nock.spi.util.Pair;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PropertyContext {

    List<? extends Pair<String, String>> getProperties();

    <T extends ArtifactReference> T getReference(T ref);
}
