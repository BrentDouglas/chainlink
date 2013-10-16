package io.machinecode.nock.core.work;

import io.machinecode.nock.spi.transport.Result;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ResultImpl implements Result {

    public static final ResultImpl CANCELLED = new ResultImpl(Status.CANCELLED);
    public static final ResultImpl RUNNING = new ResultImpl(Status.RUNNING);
    public static final ResultImpl FINISHED = new ResultImpl(Status.FINISHED);

    private final Status status;
    private final Serializable value;

    public ResultImpl(final Status status) {
        this.status = status;
        this.value = null;
    }

    public ResultImpl(final Exception e) {
        this.status = Status.ERROR;
        this.value = e;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public Serializable value() {
        return value;
    }
}
