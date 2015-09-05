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
package io.machinecode.chainlink.core.registry;

import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import io.machinecode.chainlink.spi.registry.SplitAccumulator;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class SplitAccumulatorImpl implements SplitAccumulator {

    private long count = 0;
    private final TLongSet priorStepExecutionIds = new TLongHashSet();

    @Override
    public long incrementAndGetCallbackCount() {
        return ++count;
    }

    @Override
    public long[] getPriorStepExecutionIds() {
        return priorStepExecutionIds.toArray();
    }

    @Override
    public void addPriorStepExecutionId(final long priorStepExecutionId) {
        this.priorStepExecutionIds.add(priorStepExecutionId);
    }
}
