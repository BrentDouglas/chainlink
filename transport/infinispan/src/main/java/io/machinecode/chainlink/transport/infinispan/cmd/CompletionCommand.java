package io.machinecode.chainlink.transport.infinispan.cmd;

import io.machinecode.chainlink.spi.deferred.Deferred;
import io.machinecode.chainlink.spi.transport.DeferredId;
import org.infinispan.context.InvocationContext;
import org.infinispan.remoting.transport.Address;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CompletionCommand extends DeferredCommand {

    public static final byte COMMAND_ID = 60;

    Completion completion;

    public CompletionCommand(final String cacheName) {
        super(cacheName);
    }

    public CompletionCommand(final String cacheName, final Address remote, final long jobExecutionId, final DeferredId deferredId, final Completion completion) {
        super(cacheName, remote, jobExecutionId, deferredId);
        this.completion = completion;
    }

    @Override
    public Object perform(final InvocationContext invocationContext) throws Throwable {
        final Deferred<?> deferred = transport.getDeferred(jobExecutionId, deferredId);
        switch (completion) {
            case RESOLVE:
                deferred.resolve(null);
                break;
            case REJECT:
                deferred.reject(null);
                break;
            case CANCEL:
                deferred.cancel(true);
                break;
        }
        return true;
    }

    @Override
    public byte getCommandId() {
        return COMMAND_ID;
    }

    @Override
    public Object[] getParameters() {
        return new Object[]{ remote, jobExecutionId, deferredId, completion };
    }

    @Override
    public void setParameters(final int commandId, final Object[] parameters) {
        super.setParameters(commandId, parameters);
        this.completion = (Completion)parameters[3];
    }
}
