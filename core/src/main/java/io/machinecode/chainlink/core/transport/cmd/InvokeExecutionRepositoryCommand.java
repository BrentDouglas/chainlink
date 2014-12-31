package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.transport.Command;
import io.machinecode.chainlink.spi.transport.Transport;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InvokeExecutionRepositoryCommand<T,A> implements Command<T,A> {
    private static final long serialVersionUID = 1L;

    final ExecutionRepositoryId executionRepositoryId;
    final String methodName;
    final Serializable[] parameters;

    public InvokeExecutionRepositoryCommand(final ExecutionRepositoryId executionRepositoryId, final String name,
                                            final Serializable[] params) {
        this.executionRepositoryId = executionRepositoryId;
        this.methodName = name;
        this.parameters = params;
    }

    @Override
    public T perform(final Transport<A> transport, final A origin) throws Throwable {
        //TODO Ensure local
        final ExecutionRepository repository = transport.getRegistry().getExecutionRepository(executionRepositoryId);
        Method method = null;
        for (final Method that : ExecutionRepository.class.getDeclaredMethods()) {
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
            return (T)method.invoke(repository, params);
        } catch (final IllegalArgumentException e) {
            throw e.getCause() == null ? e : e.getCause();
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
