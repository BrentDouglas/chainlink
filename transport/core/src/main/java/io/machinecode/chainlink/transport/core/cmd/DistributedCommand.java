package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.transport.core.DistributedInvoker;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface DistributedCommand<T, A, I extends DistributedInvoker<A,I> & Registry> extends Serializable {

    T perform(final I invoker, final A origin) throws Throwable;
}
