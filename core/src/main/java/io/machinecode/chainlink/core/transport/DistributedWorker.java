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
package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.core.transport.cmd.CallbackCommand;
import io.machinecode.chainlink.core.transport.cmd.ExecuteCommand;
import io.machinecode.chainlink.spi.execution.CallbackEvent;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.execution.WorkerId;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DistributedWorker implements Worker {

    protected final DistributedTransport<?> transport;
    protected final Object remote;
    protected final WorkerId workerId;

    public DistributedWorker(final DistributedTransport<?> transport, final WorkerId workerId) {
        this.transport = transport;
        this.remote = workerId.getAddress();
        this.workerId = workerId;
    }

    @Override
    public WorkerId getId() {
        return workerId;
    }

    @Override
    public void execute(final ExecutableEvent event) {
        transport.invokeRemote(remote, new ExecuteCommand(workerId, event));
    }

    @Override
    public void callback(final CallbackEvent event) {
        transport.invokeRemote(remote, new CallbackCommand(workerId, event));
    }
}
