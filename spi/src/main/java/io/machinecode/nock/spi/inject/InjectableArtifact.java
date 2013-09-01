package io.machinecode.nock.spi.inject;

import javax.batch.operations.BatchRuntimeException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InjectableArtifact<T> {

    T load(final InjectionContext context) throws BatchRuntimeException;
}
