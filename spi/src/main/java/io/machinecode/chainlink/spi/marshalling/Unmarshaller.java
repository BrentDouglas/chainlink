package io.machinecode.chainlink.spi.marshalling;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Unmarshaller {

    Serializable unmarshall(final byte[] that) throws ClassNotFoundException, IOException;

    <T extends Serializable> T unmarshall(final byte[] that, final Class<T> clazz) throws ClassNotFoundException, IOException;
}
