package io.machinecode.chainlink.spi.loader;

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
     * @return An artifact matching {@param id}
     * @throws BatchRuntimeException If the artifact does not implement {@param as}
     */
    <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws ArtifactOfWrongTypeException;
}
