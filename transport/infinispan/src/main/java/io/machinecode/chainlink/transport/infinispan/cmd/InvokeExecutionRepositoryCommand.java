package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.spi.registry.ExecutionRepositoryId;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.transport.infinispan.InfinispanRegistry;
import io.machinecode.chainlink.transport.infinispan.configuration.ChainlinkCommand;
import org.infinispan.commands.remote.BaseRpcCommand;
import org.infinispan.context.InvocationContext;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class InvokeExecutionRepositoryCommand extends BaseRpcCommand implements ChainlinkCommand {

    private static final Logger log = Logger.getLogger(InvokeExecutionRepositoryCommand.class);

    public static final byte COMMAND_ID_63 = 63;

    ExecutionRepositoryId executionRepositoryId;
    String methodName;
    Boolean willReturn;
    Serializable[] parameters;

    transient Registry registry;

    public InvokeExecutionRepositoryCommand(final String cacheName) {
        super(cacheName);
    }

    public InvokeExecutionRepositoryCommand(final String cacheName, final ExecutionRepositoryId executionRepositoryId,
                                            final String methodName, final Boolean willReturn, final Serializable... parameters) {
        super(cacheName);
        this.executionRepositoryId = executionRepositoryId;
        this.methodName = methodName;
        this.willReturn = willReturn;
        this.parameters = parameters;
    }

    @Override
    public void init(final InfinispanRegistry registry) {
        this.registry = registry;
    }

    @Override
    public Object perform(final InvocationContext context) throws Throwable {
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
            return method.invoke(repository, params);
        } catch (final IllegalArgumentException e) {
            throw e.getCause() == null ? e : e.getCause();
        } catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID_63;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ executionRepositoryId, methodName, willReturn, parameters };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        if (commandId != getCommandId()) throw new IllegalStateException(); //TODO Message
        this.executionRepositoryId = (ExecutionRepositoryId)parameters[0];
        this.methodName = (String)parameters[1];
        this.willReturn = (Boolean)parameters[2];
        this.parameters = (Serializable[])parameters[3];
    }

    @Override
    public boolean isReturnValueExpected() {
        return willReturn;
    }
}
