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
package io.machinecode.chainlink.core.execution;

import io.machinecode.chainlink.spi.execution.Executable;
import io.machinecode.chainlink.spi.execution.ExecutableEvent;
import io.machinecode.chainlink.spi.registry.ChainId;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class ExecutableEventImpl implements ExecutableEvent {
    private static final long serialVersionUID = 1L;

    final ChainId chainId;
    final Executable executable;

    public ExecutableEventImpl(final Executable executable, final ChainId chainId) {
        this.chainId = chainId;
        this.executable = executable;
    }

    @Override
    public ChainId getChainId() {
        return chainId;
    }

    @Override
    public Executable getExecutable() {
        return executable;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ExecutableEventImpl{");
        sb.append("chainId=").append(chainId);
        sb.append(", executable=").append(executable);
        sb.append('}');
        return sb.toString();
    }
}
