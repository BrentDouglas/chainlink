package io.machinecode.chainlink.spi.transport;

import io.machinecode.chainlink.spi.registry.Registry;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Command<T, A> extends Serializable {

    T perform(final Transport<A> transport, final Registry registry, final A origin) throws Throwable;
}
