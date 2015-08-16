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
package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InvokeChainCommand<T> implements Command<T> {
    private static final long serialVersionUID = 1L;

    final long jobExecutionId;
    final ChainId chainId;
    final String methodName;
    final Serializable[] parameters;

    public InvokeChainCommand(final long jobExecutionId, final ChainId chainId, final String name,
                              final Serializable[] params) {
        this.jobExecutionId = jobExecutionId;
        this.chainId = chainId;
        this.methodName = name;
        this.parameters = params;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T perform(final Configuration configuration, final Object origin) throws Throwable {
        final Chain<?> chain = configuration.getRegistry().getChain(jobExecutionId, chainId);
        LocalRegistry.assertChain(chain, jobExecutionId, chainId);
        Method method = null;
        for (final Method that : chain.getClass().getMethods()) {
            if (that.getName().equals(methodName) && that.getParameterTypes().length == parameters.length) {
                method = that;
                break;
            }
        }
        if (method == null) {
            throw new IllegalStateException(); //TODO Message
        }
        final Object[] params = new Object[parameters.length];
        System.arraycopy(parameters, 0, params, 0, parameters.length);
        try {
            final Object that = method.invoke(chain, params);
            return that instanceof Serializable
                    ? (T)that
                    : null;
        } catch (final IllegalArgumentException e) {
            throw e.getCause() == null ? e : e.getCause();
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InvokeChainCommand{");
        sb.append("jobExecutionId=").append(jobExecutionId);
        sb.append(", chainId=").append(chainId);
        sb.append(", methodName='").append(methodName).append('\'');
        sb.append(", parameters=").append(Arrays.toString(parameters));
        sb.append('}');
        return sb.toString();
    }
}
