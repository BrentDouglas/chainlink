package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.transport.core.DistributedRegistry;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InvokeChainCommand<T,A,R extends DistributedRegistry<A,R>> implements DistributedCommand<T,A,R> {

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
    public T perform(final R registry, final A origin) throws Throwable {
        final Chain<?> chain = registry.getChain(jobExecutionId, chainId);
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
}
