package io.machinecode.chainlink.jsl.core.inherit;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.element.Properties;
import io.machinecode.chainlink.spi.element.partition.Plan;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface InheritablePlan<T extends InheritablePlan<T, P>,
        P extends MergeableList<P> & Properties>
        extends Copyable<T>, Plan {

    T setPartitions(final String partitions);

    T setThreads(final String threads);

    @Override
    List<P> getProperties() ;

    T setProperties(final List<P> properties);

    class PlanTool {

        public static <T extends InheritablePlan<T, P>,
                P extends MergeableList<P> & Properties>
        T copy(final T _this, final T that) {
            that.setPartitions(_this.getPartitions());
            that.setThreads(_this.getThreads());
            that.setProperties(Util.copyList(_this.getProperties()));
            return that;
        }
    }
}
