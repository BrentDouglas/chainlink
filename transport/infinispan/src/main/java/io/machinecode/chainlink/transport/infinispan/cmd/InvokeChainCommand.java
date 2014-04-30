package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.spi.registry.ChainId;
import io.machinecode.chainlink.spi.then.Chain;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InvokeChainCommand extends DeferredCommand {

    private static final Logger log = Logger.getLogger(InvokeChainCommand.class);

    public static final byte COMMAND_ID_64 = 64;

    String methodName;
    Serializable[] parameters;
    Boolean willReturn;

    public InvokeChainCommand(final String cacheName) {
        super(cacheName);
    }

    public InvokeChainCommand(final String cacheName, final long jobExecutionId, final ChainId chainId, final String methodName, final Boolean willReturn, final Serializable... parameters) {
        super(cacheName, jobExecutionId, chainId);
        this.methodName = methodName;
        this.willReturn = willReturn;
        this.parameters = parameters;
    }

    @Override
    public Object perform(final InvocationContext context) throws Throwable {
        final Chain<?> chain = registry.getJobRegistry(jobExecutionId).getChain(chainId);
        Method method = null;
        for (final Method that : chain.getClass().getMethods()) { //TODO Should use Chain.class once worked around resolveLinkRejected etc
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
                    ? that
                    : null;
        } catch (final IllegalArgumentException e) {
            final Throwable cause = e.getCause() == null ? e : e.getCause();
            log.errorf(cause, ""); //TODO Message
            throw cause;
        } catch (final InvocationTargetException e) {
            final Throwable cause = e.getCause();
            log.errorf(cause, ""); //TODO Message
            throw cause;
        }
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID_64;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ jobExecutionId, chainId, methodName, willReturn, parameters };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        super.setParameters(commandId, parameters);
        this.methodName = (String)parameters[2];
        this.willReturn = (Boolean)parameters[3];
        this.parameters = (Serializable[])parameters[4];
    }

    @Override
    public boolean isReturnValueExpected() {
        return willReturn;
    }
}
