package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.work.DeferredImpl;
import io.machinecode.nock.core.work.PlanImpl;
import io.machinecode.nock.core.work.execution.AfterExecution;
import io.machinecode.nock.core.work.execution.RunExecution;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.element.execution.Split;
import io.machinecode.nock.spi.transport.TargetThread;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.work.Deferred;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.TransitionWork;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitImpl extends ExecutionImpl implements Split {

    private final String next;
    private final List<FlowImpl> flows;

    public SplitImpl(final String id, final String next, final List<FlowImpl> flows) {
        super(id);
        this.next = next;
        this.flows = flows;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public List<FlowImpl> getFlows() {
        return this.flows;
    }

    @Override
    public String element() {
        return ELEMENT;
    }

    @Override
    public Deferred run(final Transport transport, final Context context) throws Exception {
        final RunExecution[] flows = new RunExecution[this.flows.size()];
        for (int i = 0; i < flows.length; ++i) {
            flows[i] = new RunExecution(this.flows.get(i), context);
        }
        return transport.execute(new PlanImpl(flows, TargetThread.ANY, Flow.ELEMENT)
                .always(new PlanImpl(new AfterExecution(this, context), TargetThread.THIS, Flow.ELEMENT))
        );
    }

    @Override
    public Deferred after(final Transport transport, final Context context) throws Exception {
        final ExecutionWork next = this.transitionOrSetStatus(transport, context, Collections.<TransitionWork>emptyList(), this.next);
        if (next != null) {
            return transport.execute(next.plan(transport, context));
        }
        return new DeferredImpl();
    }
}
