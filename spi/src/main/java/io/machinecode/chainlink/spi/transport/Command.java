package io.machinecode.chainlink.spi.transport;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Command<T, A> extends Serializable {

    T perform(final Transport<A> transport, final A origin) throws Throwable;
}
