package io.machinecode.chainlink.spi.marshalling;

import java.io.IOException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public interface Cloner {

    <T> T clone(final T that) throws ClassNotFoundException, IOException;
}
