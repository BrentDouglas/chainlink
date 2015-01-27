package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.registry.ExecutableId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FindWorkerIdForExecutionCommand implements Command<WorkerId> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;
    final ExecutableId executableId;

    public FindWorkerIdForExecutionCommand(final long jobExecutionId, final ExecutableId executableId) {
        this.jobExecutionId = jobExecutionId;
        this.executableId = executableId;
    }

    @Override
    public WorkerId perform(final Configuration configuration, final Object origin) throws Throwable {
        final Worker worker = getLocalWorker(configuration, jobExecutionId, executableId);
        return worker == null
                ? null
                : worker.getId();
    }

    private static Worker getLocalWorker(final Configuration configuration, final long jobExecutionId, final ExecutableId executableId) throws Exception {
        final Executable executable = configuration.getRegistry().getExecutable(jobExecutionId, executableId);
        if (executable == null) {
            return null;
        }
        return configuration.getExecutor().getWorker(executable.getWorkerId());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("FindWorkerIdForExecutionCommand{");
        sb.append("jobExecutionId=").append(jobExecutionId);
        sb.append(", executableId=").append(executableId);
        sb.append('}');
        return sb.toString();
    }
}
