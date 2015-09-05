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
package io.machinecode.chainlink.core.transport.artifacts;

import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.registry.ExecutableId;
import io.machinecode.chainlink.spi.execution.WorkerId;
import io.machinecode.chainlink.spi.then.Chain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestExecutable implements Executable {

    final ExecutableId id;
    final TestExecutionContext context;
    WorkerId workerId;

    public TestExecutable(final long id, final String address, final TestExecutionContext context) {
        this.context = context;
        this.id = new TestId(id, address);
    }

    @Override
    public ExecutableId getId() {
        return id;
    }

    @Override
    public WorkerId getWorkerId() {
        return workerId;
    }

    @Override
    public ExecutionContext getContext() {
        return context;
    }

    @Override
    public void execute(final Configuration configuration, final Chain<?> chain, final WorkerId workerId, final ExecutionContext childContext) {
        this.workerId = workerId;
    }
}
