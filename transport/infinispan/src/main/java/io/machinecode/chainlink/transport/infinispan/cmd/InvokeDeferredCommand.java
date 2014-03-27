package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.transport.DeferredId;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;

import java.lang.reflect.Method;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InvokeDeferredCommand extends DeferredCommand {

    public static final byte COMMAND_ID = 64;

    String methodName;
    Object[] parameters;
    Boolean willReturn;

    public InvokeDeferredCommand(final String cacheName) {
        super(cacheName);
    }

    public InvokeDeferredCommand(final String cacheName, final Address remote, final long jobExecutionId, final DeferredId deferredId, final String methodName, final Boolean willReturn, final Object... parameters) {
        super(cacheName, remote, jobExecutionId, deferredId);
        this.methodName = methodName;
        this.willReturn = willReturn;
        this.parameters = parameters;
    }

    @Override
    public Object perform(final InvocationContext context) throws Throwable {
        final Deferred<?> deferred = transport.getDeferred(jobExecutionId, deferredId);
        Method method = null;
        for (final Method that : Deferred.class.getMethods()) {
            if (that.getName().equals(methodName) && that.getParameterTypes().length == parameters.length) {
                method = that;
                break;
            }
        }
        if (method == null) {
            throw new IllegalStateException(); //TODO Message
        }
        return method.invoke(deferred, parameters);
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ remote, jobExecutionId, deferredId, methodName, willReturn, parameters };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        if (commandId != getCommandId()) throw new IllegalStateException(); //TODO Message
        super.setParameters(commandId, parameters);
        this.methodName = (String)parameters[3];
        this.willReturn = (Boolean)parameters[4];
        this.parameters = (Object[])parameters[5];
    }

    @Override
    public boolean isReturnValueExpected() {
        return willReturn;
    }
}
