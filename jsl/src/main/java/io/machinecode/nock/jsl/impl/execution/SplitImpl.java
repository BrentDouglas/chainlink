package io.machinecode.nock.jsl.impl.execution;

import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.impl.Util;
import io.machinecode.nock.jsl.impl.Util.NextTransformer;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitImpl extends ExecutionImpl implements Split {

    private static final NextTransformer<Flow> FLOW_TRANSFORMER = new NextTransformer<Flow>() {
        @Override
        public Flow transform(final Flow that, final Flow next) {
            return new FlowImpl(that, next);
        }
    };

    private final String next;
    private final List<Flow> flows;

    public SplitImpl(final Split that, final Execution execution) {
        super(that);
        this.next = that.getNext() == null ? execution == null ? null : execution.getId() : that.getNext();
        this.flows = Util.immutableCopy(that.getFlows(), FLOW_TRANSFORMER);
    }

    @Override
    public String getNext() {
        return this.next;
    }

    @Override
    public List<Flow> getFlows() {
        return this.flows;
    }
}
