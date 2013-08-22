package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.spi.element.execution.Execution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class ExecutionImpl implements Execution {

    protected final String id;

    public ExecutionImpl(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
