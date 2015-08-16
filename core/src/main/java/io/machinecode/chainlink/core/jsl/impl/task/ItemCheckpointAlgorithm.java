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
package io.machinecode.chainlink.core.jsl.impl.task;

import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
final class ItemCheckpointAlgorithm extends CheckpointAlgorithmImpl {
    private static final long serialVersionUID = 1L;

    final int timeout;
    final int target;
    int current;

    public ItemCheckpointAlgorithm(final int timeout, final int target) {
        super(null, null);
        this.timeout = timeout;
        this.target = target;
    }

    @Override
    public int checkpointTimeout(final Configuration configuration, final ExecutionContext context) throws Exception {
        return timeout;
    }

    @Override
    public void beginCheckpoint(final Configuration configuration, final ExecutionContext context) throws Exception {
        //
    }

    @Override
    public boolean isReadyToCheckpoint(final Configuration configuration, final ExecutionContext context) throws Exception {
        if (current > target) {
            throw new IllegalStateException(Messages.format("CHAINLINK-030000.item.checkpoint", current, target));
        }
        return target == ++current;
    }

    @Override
    public void endCheckpoint(final Configuration configuration, final ExecutionContext context) throws Exception {
        current = 0;
    }
}
