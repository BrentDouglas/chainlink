package io.machinecode.chainlink.spi.marshalling;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Marshaller {

    byte[] marshall(final Serializable that) throws IOException;

    byte[] marshall(final Serializable... that) throws IOException;

    Serializable unmarshall(final byte[] that) throws ClassNotFoundException, IOException;

    <T> T unmarshall(final byte[] that, final Class<T> clazz) throws ClassNotFoundException, IOException;

    <T extends Serializable> T clone(final T that) throws ClassNotFoundException, IOException;
}
