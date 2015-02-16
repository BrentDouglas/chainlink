package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.transport.cmd.CallbackCommand;
import io.machinecode.chainlink.core.transport.cmd.ExecuteCommand;
import io.machinecode.chainlink.spi.execution.CallbackEvent;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DistributedWorker implements Worker {

    protected final DistributedTransport<?> transport;
    protected final Object remote;
    protected final WorkerId workerId;

    public DistributedWorker(final DistributedTransport<?> transport, final WorkerId workerId) {
        this.transport = transport;
        this.remote = workerId.getAddress();
        this.workerId = workerId;
    }

    @Override
    public WorkerId getId() {
        return workerId;
    }

    @Override
    public void execute(final ExecutableEvent event) {
        transport.invokeRemote(remote, new ExecuteCommand(workerId, event));
    }

    @Override
    public void callback(final CallbackEvent event) {
        transport.invokeRemote(remote, new CallbackCommand(workerId, event));
    }
}
