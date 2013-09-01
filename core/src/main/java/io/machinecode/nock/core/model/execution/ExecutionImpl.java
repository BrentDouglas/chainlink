package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.work.CompletedFuture;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.Worker;

import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class ExecutionImpl implements Execution, ExecutionWork {

    protected final String id;

    public ExecutionImpl(final String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Future<Void> before(final Worker worker, final Transport transport, final Context context) throws Exception {
        return CompletedFuture.INSTANCE;
    }

    @Override
    public Future<Void> after(final Worker worker, final Transport transport, final Context context) throws Exception {
        return CompletedFuture.INSTANCE;
    }
}
