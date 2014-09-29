package io.machinecode.chainlink.transport.core.cmd;

import io.machinecode.chainlink.transport.core.DistributedRegistry;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface DistributedCommand<T, A, R extends DistributedRegistry<A, R>> extends Serializable {

    T perform(final R registry, final A origin) throws Throwable;
}
