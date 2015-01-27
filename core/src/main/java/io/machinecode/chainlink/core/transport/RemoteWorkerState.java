package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.execution.WorkerId;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RemoteWorkerState implements WorkerState {

    final WorkerId id;
    final int executions;
    final int callbacks;

    public RemoteWorkerState(final WorkerState that) {
        this(that.getId(), that.getExecutions(), that.getCallbacks());
    }

    public RemoteWorkerState(final WorkerId id, final int executions, final int callbacks) {
        this.id = id;
        this.executions = executions;
        this.callbacks = callbacks;
    }

    @Override
    public WorkerId getId() {
        return id;
    }

    @Override
    public int getExecutions() {
        return executions;
    }

    @Override
    public int getCallbacks() {
        return callbacks;
    }

    public static List<WorkerState> copy(final List<WorkerState> states) {
        final List<WorkerState> ret = new ArrayList<>(states.size());
        for (final WorkerState state : states) {
            ret.add(new RemoteWorkerState(state));
        }
        return ret;
    }
}
