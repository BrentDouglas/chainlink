package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InvokeChainCommand<T,A> implements Command<T,A> {
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
    public T perform(final Transport<A> transport, final Registry registry, final A origin) throws Throwable {
        final Chain<?> chain = registry.getChain(jobExecutionId, chainId);
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
