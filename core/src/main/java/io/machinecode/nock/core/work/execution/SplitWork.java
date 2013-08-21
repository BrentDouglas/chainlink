package io.machinecode.nock.core.work.execution;

import io.machinecode.nock.spi.element.execution.Split;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitWork extends ExecutionWork implements Split {

    private final String next;
    private final List<FlowWork> flows;

    public SplitWork(final String id, final String next, final List<FlowWork> flows) {
        super(id);
        this.next = next;
        this.flows = flows;
    }

    @Override
    public List<FlowWork> getFlows() {
        return this.flows;
    }

    @Override
    public String getNext() {
        return this.next;
    }
}
