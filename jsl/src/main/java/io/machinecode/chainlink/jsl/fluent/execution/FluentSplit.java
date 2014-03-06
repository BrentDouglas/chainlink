package io.machinecode.chainlink.jsl.fluent.execution;

import io.machinecode.chainlink.jsl.inherit.execution.InheritableSplit;
import io.machinecode.chainlink.spi.loader.JobRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentSplit implements FluentExecution<FluentSplit>, InheritableSplit<FluentSplit, FluentFlow> {

    private String id;
    private String next;
    private List<FluentFlow> flows = new ArrayList<FluentFlow>(0);


    @Override
    public String getId() {
        return this.id;
    }

    public FluentSplit setId(final String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getNext() {
        return this.next;
    }

    public FluentSplit setNext(final String next) {
        this.next = next;
        return this;
    }

    @Override
    public List<FluentFlow> getFlows() {
        return this.flows;
    }

    @Override
    public FluentSplit setFlows(final List<FluentFlow> flows) {
        this.flows = flows;
        return this;
    }

    public FluentSplit addFlow(final FluentFlow flow) {
        this.flows.add(flow);
        return this;
    }

    @Override
    public FluentSplit inherit(final JobRepository repository, final String defaultJobXml) {
        return SplitTool.inherit(this, repository, defaultJobXml);
    }

    @Override
    public FluentSplit copy() {
        return copy(new FluentSplit());
    }

    @Override
    public FluentSplit copy(final FluentSplit that) {
        return SplitTool.copy(this, that);
    }
}
