package io.machinecode.nock.core.model.execution;

import io.machinecode.nock.core.work.CompletedFuture;
import io.machinecode.nock.core.work.execution.AfterExecution;
import io.machinecode.nock.core.work.execution.RunExecution;
import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.element.execution.Split;
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
    public Future<Void> run(final Worker worker, final Transport transport, final Context context) throws Exception {
        final RunExecution[] flows = new RunExecution[this.flows.size()];
        for (int i = 0; i < flows.length; ++i) {
            flows[i] = new RunExecution(worker, this.flows.get(i), context);
        }
        return transport.executeOnAnyThreadThenOnThisThread(flows, new AfterExecution(worker, this, context));
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
