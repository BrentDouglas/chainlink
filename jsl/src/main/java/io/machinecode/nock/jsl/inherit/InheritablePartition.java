package io.machinecode.nock.jsl.inherit;

import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.element.partition.Analyser;
import io.machinecode.nock.spi.element.partition.Collector;
import io.machinecode.nock.spi.element.partition.Partition;
import io.machinecode.nock.spi.element.partition.Reducer;
import io.machinecode.nock.spi.element.partition.Strategy;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
