package io.machinecode.nock.spi.inject;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ArtifactReference {

    String ref();

    <T> T load(final Class<T> clazz, final InjectionContext injectionContext) throws Exception;
}
