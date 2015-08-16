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
package io.machinecode.chainlink.core.factory.transition;

import io.machinecode.chainlink.core.expression.Expression;
import io.machinecode.chainlink.core.expression.JobPropertyContext;
import io.machinecode.chainlink.core.expression.PartitionPropertyContext;
import io.machinecode.chainlink.core.jsl.impl.transition.StopImpl;
import io.machinecode.chainlink.spi.jsl.transition.Stop;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class StopFactory {

    public static StopImpl produceExecution(final Stop that, final JobPropertyContext context) {
        final String on = Expression.resolveExecutionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolveExecutionProperty(that.getExitStatus(), context);
        final String restart = Expression.resolveExecutionProperty(that.getRestart(), context);
        return new StopImpl(on, exitStatus, restart);
    }

    public static StopImpl producePartitioned(final StopImpl that, final PartitionPropertyContext context) {
        final String on = Expression.resolvePartitionProperty(that.getOn(), context);
        final String exitStatus = Expression.resolvePartitionProperty(that.getExitStatus(), context);
        final String restart = Expression.resolvePartitionProperty(that.getRestart(), context);
        return new StopImpl(on, exitStatus, restart);
    }
}
