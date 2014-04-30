package io.machinecode.chainlink.spi.registry;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface WorkerId extends Serializable {

    @Override
    boolean equals(final Object that);

    @Override
    int hashCode();
}
