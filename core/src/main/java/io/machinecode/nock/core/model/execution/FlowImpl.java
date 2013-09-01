package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.core.work.CompletedFuture;
import io.machinecode.nock.core.work.execution.AfterExecution;
import io.machinecode.nock.core.work.execution.RunExecution;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.TransitionWork;
import io.machinecode.nock.spi.work.Worker;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FlowImpl extends ExecutionImpl implements Flow {

    private final String next;
    private final List<ExecutionImpl> executions;
    private final List<TransitionImpl> transitions;

    public FlowImpl(final String id, final String next, final List<ExecutionImpl> executions, final List<TransitionImpl> transitions) {
        super(id);
        this.next = next;
        this.executions = executions;
        this.transitions = transitions;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public List<ExecutionImpl> getExecutions() {
        return this.executions;
    }

    @Override
    public List<TransitionImpl> getTransitions() {
        return this.transitions;
    }

    @Override
    public Future<Void> run(final Worker worker, final Transport transport, final Context context) throws Exception {
        final ExecutionImpl first = this.executions.get(0); //There will have been a validation error raised if this is empty
        return transport.executeOnAnyThread(new RunExecution(worker, first, context)
                .then(transport, new AfterExecution(worker, this, context))
        );
    }

    @Override
    public Future<Void> after(final Worker worker, final Transport transport, final Context context) throws Exception {
        final ExecutionWork next = worker.transitionOrSetStatus(transport, context, Collections.<TransitionWork>emptyList(), this.next);
        if (next != null) {
            return worker.runExecution(next, transport, context);
        }
        return CompletedFuture.INSTANCE;
    }
}
