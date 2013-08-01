package io.machinecode.nock.jsl.impl.type;

import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.api.execution.Split;
import io.machinecode.nock.jsl.impl.Util;
import io.machinecode.nock.jsl.impl.Util.Transformer;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitImpl extends ExecutionImpl implements Split {

    private static final Transformer<Flow> FLOW_TRANSFORMER = new Transformer<Flow>() {
        @Override
        public Flow transform(final Flow that) {
            return new FlowImpl(that);
        }
    };

    private final String next;
    private final List<Flow> flows;

    public SplitImpl(final Split that) {
        super(that);
        this.next = that.getNext();
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
