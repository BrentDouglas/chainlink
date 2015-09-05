/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.spi.jsl.inherit;

import io.machinecode.chainlink.spi.jsl.partition.Analyser;
import io.machinecode.chainlink.spi.jsl.partition.Collector;
import io.machinecode.chainlink.spi.jsl.partition.Partition;
import io.machinecode.chainlink.spi.jsl.partition.Reducer;
import io.machinecode.chainlink.spi.jsl.partition.Strategy;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
            that.setStrategy((S) Rules.copy(_this.getStrategy()));
            that.setCollector(Rules.copy(_this.getCollector()));
            that.setAnalyzer(Rules.copy(_this.getAnalyzer()));
            that.setReducer(Rules.copy(_this.getReducer()));
            return that;
        }
    }
}
