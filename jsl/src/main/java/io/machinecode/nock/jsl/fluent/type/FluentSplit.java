package io.machinecode.nock.jsl.fluent.type;

import io.machinecode.nock.jsl.api.type.Flow;
import io.machinecode.nock.jsl.api.type.Split;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentSplit extends FluentType<FluentSplit> implements Split {

    private String next;
    private List<Flow> flows = new ArrayList<Flow>(0);

    @Override
    public String getNext() {
        return this.next;
    }

    public FluentSplit setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public List<Flow> getFlows() {
        return this.flows;
    }

    public FluentSplit addFlow(final Flow flow) {
        this.flows.add(flow);
        return this;
    }
}
