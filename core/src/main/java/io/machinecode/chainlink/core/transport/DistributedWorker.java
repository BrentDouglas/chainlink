package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.transport.cmd.CallbackCommand;
import io.machinecode.chainlink.core.transport.cmd.Command;
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

    protected final BaseTransport<?> transport;
    protected final Object remote;
    protected final WorkerId workerId;

    public DistributedWorker(final BaseTransport<?> transport, final WorkerId workerId) {
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
        final Command<Object> command = new ExecuteCommand(workerId, event);
        transport.invokeRemote(remote, command, transport.getTimeout(), transport.getTimeUnit());
    }

    @Override
    public void callback(final CallbackEvent event) {
        final Command<Object> command = new CallbackCommand(workerId, event);
        transport.invokeRemote(remote, command, transport.getTimeout(), transport.getTimeUnit());
    }
}
