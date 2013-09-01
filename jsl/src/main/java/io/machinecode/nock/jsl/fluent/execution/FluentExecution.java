package io.machinecode.nock.jsl.fluent.execution;

import io.machinecode.nock.spi.element.execution.Execution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class FluentExecution<T extends FluentExecution<T>> implements Execution {

    private String id;

    @Override
    public String getId() {
        return this.id;
    }

    public T setId(final String id) {
        this.id = id;
        return (T)this;
    }
}
