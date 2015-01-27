package io.machinecode.chainlink.core.transport.cmd;

import io.machinecode.chainlink.core.registry.LocalRegistry;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InvokeExecutionRepositoryCommand<T> implements Command<T> {
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
    @SuppressWarnings("unchecked")
    public T perform(final Configuration configuration, final Object origin) throws Throwable {
        //TODO Ensure local
        final ExecutionRepository repository = configuration.getRegistry().getExecutionRepository(executionRepositoryId);
        LocalRegistry.assertExecutionRepository(repository, executionRepositoryId);
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("InvokeExecutionRepositoryCommand{");
        sb.append("executionRepositoryId=").append(executionRepositoryId);
        sb.append(", methodName='").append(methodName).append('\'');
        sb.append(", parameters=").append(Arrays.toString(parameters));
        sb.append('}');
        return sb.toString();
    }
}
