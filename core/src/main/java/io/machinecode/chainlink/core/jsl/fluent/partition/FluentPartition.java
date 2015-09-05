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
package io.machinecode.chainlink.core.jsl.fluent.partition;

import io.machinecode.chainlink.spi.jsl.inherit.InheritablePartition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentPartition
        implements InheritablePartition<FluentPartition, FluentStrategy, FluentCollector, FluentAnalyser, FluentReducer> {

    private FluentStrategy strategy;
    private FluentCollector collector;
    private FluentAnalyser analyser;
    private FluentReducer reducer;

    @Override
    public FluentStrategy getStrategy() {
        return this.strategy;
    }

    public FluentPartition setStrategy(final FluentStrategy strategy) {
        this.strategy = strategy;
        return this;
    }

    public FluentPartition setMapper(final FluentMapper strategy) {
        this.strategy = strategy;
        return this;
    }

    public FluentPartition setPlan(final FluentPlan strategy) {
        this.strategy = strategy;
        return this;
    }

    @Override
    public FluentCollector getCollector() {
        return this.collector;
    }

    @Override
    public FluentPartition setCollector(final FluentCollector collector) {
        this.collector = collector;
        return this;
    }

    @Override
    public FluentAnalyser getAnalyzer() {
        return this.analyser;
    }

    @Override
    public FluentPartition setAnalyzer(final FluentAnalyser analyser) {
        this.analyser = analyser;
        return this;
    }

    @Override
    public FluentReducer getReducer() {
        return this.reducer;
    }

    public FluentPartition setReducer(final FluentReducer reducer) {
        this.reducer = reducer;
        return this;
    }

    @Override
    public FluentPartition copy() {
        return copy(new FluentPartition());
    }

    @Override
    public FluentPartition copy(final FluentPartition that) {
        return PartitionTool.copy(this, that);
    }
}
