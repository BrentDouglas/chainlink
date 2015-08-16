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
package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.execution.Worker;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class RemoteExecution {
    private final Worker worker;
    private final ChainId localId;
    private final ChainId remoteId;
    private final Chain<?> chain;

    public RemoteExecution(final Worker worker, final ChainId localId, final ChainId remoteId, final Chain<?> chain) {
        this.worker = worker;
        this.localId = localId;
        this.remoteId = remoteId;
        this.chain = chain;
    }

    public Worker getWorker() {
        return worker;
    }

    public ChainId getLocalId() {
        return localId;
    }

    public ChainId getRemoteId() {
        return remoteId;
    }

    public Chain<?> getChain() {
        return chain;
    }
}
