package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.model.transition.TransitionImpl;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.transport.Plan;
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
public class FlowImpl extends ExecutionImpl implements Flow {

    private static final Logger log = Logger.getLogger(FlowImpl.class);

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
        log.debugf(Message.get("flow.run"), context.getJobExecutionId(), id);
        return this.executions.get(0).plan(transport, context);
    }

    @Override
    public Plan after(final Transport transport, final Context context) throws Exception {
        log.debugf(Message.get("flow.after"), context.getJobExecutionId(), id);
        final ExecutionWork next = this.transition(transport, context, Collections.<TransitionWork>emptyList(), this.next);
        if (next != null) {
            return next.plan(transport, context);
        }
        return null;
    }
}
