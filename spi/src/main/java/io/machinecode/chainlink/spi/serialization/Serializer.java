package io.machinecode.chainlink.spi.serialization;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Serializer {

    byte[] bytes(final Serializable that) throws IOException;

    byte[] bytes(final Serializable... that) throws IOException;

    Serializable read(final byte[] that) throws ClassNotFoundException, IOException;

    <T> T read(final byte[] that, final Class<T> clazz) throws ClassNotFoundException, IOException;

    <T extends Serializable> T clone(final T that) throws ClassNotFoundException, IOException;
}
