package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.transport.infinispan.InfinispanTransport;
import io.machinecode.chainlink.transport.infinispan.configuration.ChainlinkCommand;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.transport.ExecutionRepositoryId;
import org.infinispan.commands.remote.BaseRpcCommand;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;

import java.lang.reflect.Method;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InvokeExecutionRepositoryCommand extends BaseRpcCommand implements ChainlinkCommand {

    public static final byte COMMAND_ID = 63;

    Address remote;
    ExecutionRepositoryId executionRepositoryId;
    String methodName;
    Object[] parameters;

    transient InfinispanTransport transport;

    public InvokeExecutionRepositoryCommand(final String cacheName) {
        super(cacheName);
    }

    public InvokeExecutionRepositoryCommand(final String cacheName, final Address remote, final ExecutionRepositoryId executionRepositoryId,
                                            final String methodName, final Object... parameters) {
        super(cacheName);
        this.remote = remote;
        this.executionRepositoryId = executionRepositoryId;
        this.methodName = methodName;
        this.parameters = parameters;
    }

    @Override
    public void setTransport(final InfinispanTransport transport) {
        this.transport = transport;
    }

    @Override
    public Object perform(final InvocationContext context) throws Throwable {
        final ExecutionRepository repository = transport.getLocalRepository(executionRepositoryId);
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
        return method.invoke(repository, parameters);
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ remote, executionRepositoryId, methodName, parameters };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        if (commandId != getCommandId()) throw new IllegalStateException(); //TODO Message
        this.remote = (Address)parameters[0];
        this.executionRepositoryId = (ExecutionRepositoryId)parameters[1];
        this.methodName = (String)parameters[2];
        this.parameters = (Object[])parameters[3];
    }

    @Override
    public boolean isReturnValueExpected() {
        return false;
    }
}
