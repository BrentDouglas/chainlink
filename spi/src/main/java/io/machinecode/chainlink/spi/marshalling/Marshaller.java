package io.machinecode.chainlink.spi.marshalling;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public interface Marshaller {

    byte[] marshall(final Serializable that) throws IOException;

    byte[] marshall(final Serializable... that) throws IOException;
}
