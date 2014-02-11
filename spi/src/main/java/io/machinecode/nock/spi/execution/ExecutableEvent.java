package io.machinecode.nock.spi.execution;

import io.machinecode.nock.spi.context.ExecutionContext;
import io.machinecode.nock.spi.deferred.Deferred;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExecutableEvent<T extends Executable> extends Serializable {

    T getExecutable();

    ExecutionContext[] getContexts();
}