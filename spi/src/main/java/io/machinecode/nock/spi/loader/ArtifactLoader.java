package io.machinecode.nock.spi.loader;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ArtifactLoader {

    /**
     * @param id
     * @param as Interface
     * @param loader
     * @param <T>
     * @return The first artifact matching {@param id} and implementing {@param as}
     * @throws BatchRuntimeException
     */
    <T> T load(final String id, final Class<T> as, final ClassLoader loader);
}
