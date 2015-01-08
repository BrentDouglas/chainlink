package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.core.transport.WorkerIdAndAddress;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

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
                ? new WorkerIdAndAddress<>(
                        transport.getWorker(jobExecutionId, executableId).id(),
                        transport.getLocal()
                )
                : null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FindWorkerForExecutionCommand{");
        sb.append("jobExecutionId=").append(jobExecutionId);
        sb.append(", executableId=").append(executableId);
        sb.append('}');
        return sb.toString();
    }
}
