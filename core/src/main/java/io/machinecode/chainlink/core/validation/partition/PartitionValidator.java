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
package io.machinecode.chainlink.core.validation.partition;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.jsl.partition.Partition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PartitionValidator extends ValidatingVisitor<Partition> {

    public static final PartitionValidator INSTANCE = new PartitionValidator();

    protected PartitionValidator() {
        super(Partition.ELEMENT);
    }

    @Override
    public void doVisit(final Partition that, final VisitorNode context) {
        if (that.getReducer() != null) {
            ReducerValidator.INSTANCE.visit(that.getReducer(), context);
        }
        if (that.getStrategy() != null) {
            StrategyValidator.validate(that.getStrategy(), context);
        }
        if (that.getAnalyzer() != null) {
            AnalyserValidator.INSTANCE.visit(that.getAnalyzer(), context);
        }
        if (that.getCollector() != null) {
            CollectorValidator.INSTANCE.visit(that.getCollector(), context);
        }
    }
}
