package io.machinecode.chainlink.spi.registry;

import io.machinecode.chainlink.spi.then.Chain;
import io.machinecode.then.api.Promise;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public interface JobEventListener {

    Promise<?,?,?> onRegister(final long jobExecutionId, final Chain<?> job);

    Promise<?,?,?> onUnregister(final long jobExecutionId, final Chain<?> job);
}
