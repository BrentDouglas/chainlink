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
package io.machinecode.chainlink.spi.execution;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.then.Chain;

/**
 * <p>A piece of work to be done.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Executable {

    /**
     * @return The id of this {@link Executable}.
     */
    ExecutableId getId();

    /**
     * @return The id of the {@link Worker} that this executable needs to run
     *         or {@code null} if it can be run by any worker.
     */
    WorkerId getWorkerId();

    /**
     * @return
     */
    ExecutionContext getContext();

    void execute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId, final ExecutionContext previous);
}