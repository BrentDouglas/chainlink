package io.machinecode.chainlink.spi.registry;

import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface WorkerId extends Serializable {

    Object getAddress();

    @Override
    boolean equals(final Object that);

    @Override
    int hashCode();
}
