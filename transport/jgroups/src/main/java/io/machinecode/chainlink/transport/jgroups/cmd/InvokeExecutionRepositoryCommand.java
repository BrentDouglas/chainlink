package io.machinecode.chainlink.transport.jgroups.cmd;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.transport.jgroups.JGroupsRegistry;
import org.jgroups.Address;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InvokeExecutionRepositoryCommand<T> implements Command<T> {

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
    public T invoke(final JGroupsRegistry registry, final Address origin) throws Throwable {
        //TODO Ensure local
        final ExecutionRepository repository = registry.getExecutionRepository(executionRepositoryId);
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
