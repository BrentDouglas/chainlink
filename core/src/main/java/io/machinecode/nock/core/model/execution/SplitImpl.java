package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.work.PlanImpl;
import io.machinecode.nock.core.work.Status;
import io.machinecode.nock.core.work.execution.AfterExecution;
import io.machinecode.nock.core.work.execution.FailExecution;
import io.machinecode.nock.core.work.execution.RunExecution;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.element.execution.Split;
import io.machinecode.nock.spi.transport.Plan;
import io.machinecode.nock.spi.transport.TargetThread;
import io.machinecode.nock.spi.transport.Transport;
import io.machinecode.nock.spi.util.Message;
import io.machinecode.nock.spi.work.ExecutionWork;
import io.machinecode.nock.spi.work.TransitionWork;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitImpl extends ExecutionImpl implements Split {

    private static final Logger log = Logger.getLogger(SplitImpl.class);

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
    public Plan plan(final Transport transport, final Context context) {
        log.debugf(Message.get("split.plan"), context.getJobExecutionId(), id);
        if (Status.isStopping(context) || Status.isComplete(context)) {
            return null; //TODO
        }

        final AfterExecution after = new AfterExecution(this, context);
        final FailExecution fail = new FailExecution(this, context); //TODO

        final PlanImpl afterPlan = new PlanImpl(after, TargetThread.THIS, element());
        final PlanImpl failPlan = new PlanImpl(fail, TargetThread.THIS, element());
        final PlanImpl afterFailPlan = new PlanImpl(fail, TargetThread.THIS, element());

        final RunExecution[] flows = new RunExecution[this.flows.size()];
        for (int i = 0; i < flows.length; ++i) {
            flows[i] = new RunExecution(this.flows.get(i), context);
        }
        return new PlanImpl(flows, TargetThread.ANY, Flow.ELEMENT).fail(failPlan)
                .always(afterPlan
                        .fail(afterFailPlan)
                );
    }

    @Override
    public Plan run(final Transport transport, final Context context) throws Exception {
        log.debugf(Message.get("split.run"), context.getJobExecutionId(), id);
        throw new IllegalStateException();
        //final RunExecution[] flows = new RunExecution[this.flows.size()];
        //for (int i = 0; i < flows.length; ++i) {
        //    flows[i] = new RunExecution(this.flows.get(i), context);
        //}
        //return new PlanImpl(flows, TargetThread.ANY, Flow.ELEMENT);
    }

    @Override
    public Plan after(final Transport transport, final Context context) throws Exception {
        log.debugf(Message.get("split.after"), context.getJobExecutionId(), id);
        final ExecutionWork next = this.transition(transport, context, Collections.<TransitionWork>emptyList(), this.next);
        if (next != null) {
            return next.plan(transport, context);
        }
        return null;
    }
}
