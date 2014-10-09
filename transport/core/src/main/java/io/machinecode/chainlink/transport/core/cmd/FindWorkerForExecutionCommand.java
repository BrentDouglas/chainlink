package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;
import io.machinecode.chainlink.transport.core.WorkerIdAndAddress;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindWorkerForExecutionCommand<A> implements Command<WorkerIdAndAddress<A>,A> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;
    final ExecutableId executableId;

    public FindWorkerForExecutionCommand(final long jobExecutionId, final ExecutableId executableId) {
        this.jobExecutionId = jobExecutionId;
        this.executableId = executableId;
    }

    @Override
    public WorkerIdAndAddress<A> perform(final Transport<A> transport, final A origin) throws Throwable {
        return transport.hasWorker(jobExecutionId, executableId)
                ? new WorkerIdAndAddress<A>(
                        transport.getWorker(jobExecutionId, executableId).id(),
                        transport.getLocal()
                )
                : null;
    }
}
