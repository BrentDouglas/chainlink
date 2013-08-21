package io.machinecode.nock.core.descriptor.execution;

import io.machinecode.nock.spi.element.execution.Execution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecutionImpl implements Execution {

    private final String id;

    public ExecutionImpl(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
