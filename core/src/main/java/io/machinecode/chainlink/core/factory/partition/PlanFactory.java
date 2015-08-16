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

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.factory.PropertiesFactory;
import io.machinecode.chainlink.core.jsl.impl.PropertiesImpl;
import io.machinecode.chainlink.core.jsl.impl.partition.PlanImpl;
import io.machinecode.chainlink.spi.jsl.Properties;
import io.machinecode.chainlink.spi.jsl.partition.Plan;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class PlanFactory {

    public static PlanImpl produceExecution(final Plan that, final JobPropertyContext context) {
        final String partitions = Expression.resolveExecutionProperty(that.getPartitions(), context);
        final String threads = Expression.resolveExecutionProperty(that.getThreads() == null ? that.getPartitions() : that.getThreads(), context);
        final List<PropertiesImpl> properties = new ArrayList<PropertiesImpl>(that.getProperties().size());
        for (final Properties x : that.getProperties()) {
            properties.add(PropertiesFactory.produceExecution(x, context));
        }
        return new PlanImpl(partitions, threads, properties);
    }

    public static PlanImpl producePartitioned(final PlanImpl that, final PartitionPropertyContext context) {
        final String partitions = Expression.resolvePartitionProperty(that.getPartitions(), context);
        final String threads = Expression.resolvePartitionProperty(that.getThreads(), context);
        final List<PropertiesImpl> properties = new ArrayList<PropertiesImpl>(that.getProperties().size());
        for (final PropertiesImpl x : that.getProperties()) {
            properties.add(PropertiesFactory.producePartitioned(x, context));
        }
        return new PlanImpl(partitions, threads, properties);
    }
}
