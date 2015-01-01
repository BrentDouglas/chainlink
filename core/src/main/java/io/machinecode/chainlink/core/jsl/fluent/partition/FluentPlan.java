package io.machinecode.chainlink.core.jsl.fluent.partition;

import io.machinecode.chainlink.core.jsl.fluent.FluentProperties;
import io.machinecode.chainlink.spi.jsl.inherit.InheritablePlan;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentPlan implements FluentStrategy<FluentPlan>, InheritablePlan<FluentPlan, FluentProperties> {

    private String partitions = ONE;
    private String threads;
    private List<FluentProperties> properties = new ArrayList<>(0);


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
    public List<FluentProperties> getProperties() {
        return this.properties;
    }

    @Override
    public FluentPlan setProperties(final List<FluentProperties> properties) {
        this.properties = properties;
        return this;
    }

    public FluentPlan addProperty(final String partition, final String name, final String value) {
        for (final FluentProperties that : this.properties) {
            if (partition.equals(that.getPartition())) {
                that.addProperty(name, value);
                return this;
            }
        }
        this.properties.add(new FluentProperties().setPartition(partition).addProperty(name, value));
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
