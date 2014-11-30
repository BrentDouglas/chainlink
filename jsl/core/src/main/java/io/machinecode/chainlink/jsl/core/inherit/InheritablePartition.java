package io.machinecode.chainlink.jsl.core.inherit;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.element.partition.Analyser;
import io.machinecode.chainlink.spi.element.partition.Collector;
import io.machinecode.chainlink.spi.element.partition.Partition;
import io.machinecode.chainlink.spi.element.partition.Reducer;
import io.machinecode.chainlink.spi.element.partition.Strategy;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface InheritablePartition<T extends InheritablePartition<T, S, C, A, R>,
        S extends Copyable & Strategy,
        C extends Copyable<C> & Collector,
        A extends Copyable<A> & Analyser,
        R extends Copyable<R> & Reducer>
        extends Copyable<T>, Partition {

    @Override
    S getStrategy();

    T setStrategy(final S strategy);

    @Override
    C getCollector();

    T setCollector(final C collector);

    @Override
    A getAnalyzer();

    T setAnalyzer(final A analyzer);

    @Override
    R getReducer();

    T setReducer(final R reducer);

    class PartitionTool {

        public static <T extends InheritablePartition<T, S, C, A, R>,
                S extends Copyable & Strategy,
                C extends Copyable<C> & Collector,
                A extends Copyable<A> & Analyser,
                R extends Copyable<R> & Reducer>
        T copy(final T _this, final T that) {
            that.setStrategy((S)Util.copy(_this.getStrategy()));
            that.setCollector(Util.copy(_this.getCollector()));
            that.setAnalyzer(Util.copy(_this.getAnalyzer()));
            that.setReducer(Util.copy(_this.getReducer()));
            return that;
        }
    }
}
