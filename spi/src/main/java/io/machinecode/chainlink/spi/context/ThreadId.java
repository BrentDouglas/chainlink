package io.machinecode.chainlink.spi.context;

import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ThreadId extends Serializable {

    @Override
    boolean equals(final Object that);

    @Override
    int hashCode();
}
