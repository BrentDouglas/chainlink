package io.machinecode.nock.jsl.fluent.partition;

import io.machinecode.nock.jsl.fluent.FluentProperties;
import io.machinecode.nock.jsl.inherit.InheritablePlan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentPlan implements FluentStrategy<FluentPlan>, InheritablePlan<FluentPlan, FluentProperties> {

    private String partitions = ONE;
    private String threads;
    private FluentProperties properties;


    @Override
    public String getPartitions() {
        return this.partitions;
    }

    public FluentPlan setPartitions(final String partitions) {
        this.partitions = partitions;
        return this;
    }

    @Override
    public String getThreads() {
        return this.threads;
    }

    public FluentPlan setThreads(final String threads) {
        this.threads = threads;
        return this;
    }

    @Override
    public FluentProperties getProperties() {
        return this.properties;
    }

    @Override
    public FluentPlan setProperties(final FluentProperties properties) {
        this.properties = properties;
        return this;
    }

    public FluentPlan addProperty(final String name, final String value) {
        if (this.properties == null) {
            this.properties = new FluentProperties();
        }
        this.properties.addProperty(name, value);
        return this;
    }

    @Override
    public FluentPlan copy() {
        return copy(new FluentPlan());
    }

    @Override
    public FluentPlan copy(final FluentPlan that) {
        return PlanTool.copy(this, that);
    }
}
