package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.TransitionWork;

import java.util.Collections;
import java.util.List;

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
    public String element() {
        return ELEMENT;
    }

    @Override
    public Plan run(final Transport transport, final Context context) throws Exception {
        //final ExecutionImpl first = this.executions.get(0); //There will have been a validation error raised if this is empty
        //return transport.execute(context.getJobExecutionId(), this,
        //        new PlanImpl(new RunExecution(first, context), TargetThread.ANY, first.elementName())
        //                .then(new PlanImpl(new AfterExecution(this, context), TargetThread.THIS, first.elementName()))
        //);
        return this.executions.get(0).plan(transport, context);
    }

    @Override
    public Plan after(final Transport transport, final Context context) throws Exception {
        final ExecutionWork next = this.transitionOrSetStatus(transport, context, Collections.<TransitionWork>emptyList(), this.next);
        if (next != null) {
            //return transport.execute(context.getJobExecutionId(), this, next.plan(transport, context));
            return next.plan(transport, context);
        }
        //return new DeferredImpl<Void>();
        return null;
    }
}
