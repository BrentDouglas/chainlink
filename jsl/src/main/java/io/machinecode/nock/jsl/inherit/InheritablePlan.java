package io.machinecode.nock.jsl.inherit;

import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.partition.Plan;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritablePlan<T extends InheritablePlan<T, P>,
        P extends MergeableList<P> & Properties>
        extends Copyable<T>, Plan {

    T setPartitions(final String partitions);

    T setThreads(final String threads);

    @Override
    P getProperties() ;

    T setProperties(final P properties);

    class PlanTool {

        public static <T extends InheritablePlan<T, P>,
                P extends MergeableList<P> & Properties>
        T copy(final T _this, final T that) {
            that.setPartitions(_this.getPartitions());
            that.setThreads(_this.getThreads());
            that.setProperties(Util.copy(_this.getProperties()));
            return that;
        }
    }
}
