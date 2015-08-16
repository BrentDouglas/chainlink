/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package io.machinecode.chainlink.core.factory.partition;

import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.partition.AnalyserImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.CollectorImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.MapperImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PartitionImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.ReducerImpl;
import io.machinecode.chainlink.spi.jsl.partition.Mapper;
import io.machinecode.chainlink.spi.jsl.partition.Partition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MapperPartitionFactory {

    public static PartitionImpl<MapperImpl> produceExecution(final Partition<? extends Mapper> that, final JobPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.produceExecution(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.produceExecution(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.produceExecution(that.getReducer(), context);
        final MapperImpl mapper = that.getStrategy() == null ? null : MapperFactory.produceExecution(that.getStrategy(), context);
        return new PartitionImpl<MapperImpl>(
                collector,
                analyser,
                reducer,
                mapper
        );
    }

    public static PartitionImpl<MapperImpl> producePartitioned(final PartitionImpl<MapperImpl> that, final PartitionPropertyContext context) {
        final CollectorImpl collector = that.getCollector() == null ? null : CollectorFactory.producePartitioned(that.getCollector(), context);
        final AnalyserImpl analyser = that.getAnalyzer() == null ? null : AnalyserFactory.producePartitioned(that.getAnalyzer(), context);
        final ReducerImpl reducer = that.getReducer() == null ? null : ReducerFactory.producePartitioned(that.getReducer(), context);
        final MapperImpl strategy = that.getStrategy() == null ? null : MapperFactory.producePartitioned(that.getStrategy(), context);
        return new PartitionImpl<MapperImpl>(
                collector,
                analyser,
                reducer,
                strategy
        );
    }
}
