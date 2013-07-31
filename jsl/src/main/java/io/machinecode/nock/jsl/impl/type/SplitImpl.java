package io.machinecode.nock.jsl.impl.type;

import io.machinecode.nock.jsl.api.type.Flow;
import io.machinecode.nock.jsl.api.type.Split;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitImpl extends TypeImpl implements Split {

    private final String next;
    private final List<Flow> flows;

    public SplitImpl(final Split that) {
        super(that);
        this.next = that.getNext();
        this.flows = new ArrayList<Flow>(that.getFlows().size());
        for (final Flow flow : that.getFlows()) {
            this.flows.add(new FlowImpl(flow));
        }
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
