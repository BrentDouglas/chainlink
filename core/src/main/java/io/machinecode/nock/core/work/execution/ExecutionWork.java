package io.machinecode.nock.core.work.execution;

import io.machinecode.nock.core.work.Work;
import io.machinecode.nock.spi.element.execution.Execution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class ExecutionWork implements Work, Execution {

    private final String id;

    protected ExecutionWork(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
