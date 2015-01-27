package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GetWorkerIdsCommand implements Command<Iterable<WorkerId>> {
    private static final long serialVersionUID = 1L;

    final int required;

    public GetWorkerIdsCommand(final int required) {
        this.required = required;
    }

    @Override
    public List<WorkerId> perform(final Configuration configuration, final Object origin) throws Throwable {
        final List<WorkerId> ret = new ArrayList<>(required);
        for (final Worker worker : configuration.getExecutor().getWorkers(required)) {
            ret.add(worker.getId());
        }
        return ret;
    }
}
