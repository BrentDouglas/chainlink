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

import io.machinecode.chainlink.spi.context.ExecutionContext;
import io.machinecode.chainlink.spi.execution.CallbackEvent;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.ExecutableId;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class CallbackEventImpl implements CallbackEvent {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;
    final ChainId chainId;
    final ExecutableId executableId;
    final ExecutionContext context;

    public CallbackEventImpl(final long jobExecutionId, final ExecutableId executableId, final ChainId chainId, final ExecutionContext context) {
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
        this.executableId = executableId;
        this.context = context;
    }

    @Override
    public long getJobExecutionId() {
        return jobExecutionId;
    }

    @Override
    public ChainId getChainId() {
        return chainId;
    }

    @Override
    public ExecutableId getExecutableId() {
        return executableId;
    }

    @Override
    public ExecutionContext getContext() {
        return context;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CallbackEventImpl{");
        sb.append("jobExecutionId=").append(jobExecutionId);
        sb.append(", chainId=").append(chainId);
        sb.append(", executableId=").append(executableId);
        sb.append(", context=").append(context);
        sb.append('}');
        return sb.toString();
    }
}
