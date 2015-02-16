package io.machinecode.chainlink.spi.marshalling;

import java.io.IOException;
import java.io.Serializable;

/**
 * <p>Responsible for serializing, deserializing and cloning values.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Marshalling {

    /**
     * <p>Copy the value.</p>
     *
     * @param that The value to copy.
     * @param <T> The value type.
     * @return A value that is {@link #equals(Object)} to the parameter but not is not the same object.
     * @throws ClassNotFoundException
     * @throws IOException
     */
    <T> T clone(final T that) throws ClassNotFoundException, IOException;

    /**
     * <p>Write the value to a byte array.</p>
     *
     * @param that The value to write.
     * @return The serialized data.
     * @throws IOException
     */
    byte[] marshall(final Serializable that) throws IOException;

    /**
     * <p>Write each value to a byte array.</p>
     *
     * @param that The values to write.
     * @return The serialized data of each value.
     * @throws IOException
     */
    byte[] marshall(final Serializable... that) throws IOException;

    /**
     * <p>Read an object from a byte array.</p>
     *
     * @param that The bytes to read from.
     * @param loader The classloader to load classes from.
     * @return The deserialized object.
     * @throws ClassNotFoundException If the object is of a type that is not available to the provided classloader.
     * @throws IOException
     */
    Serializable unmarshall(final byte[] that, final ClassLoader loader) throws ClassNotFoundException, IOException;

    /**
     * <p>Read a typed object from a byte array.</p>
     *
     * @param that The bytes to read from.
     * @param clazz The class of the object to load.
     * @param loader The classloader to load classes from.
     * @param <T> The object type.
     * @return The deserialized object.
     * @throws ClassNotFoundException If the object is of a type that is not available to the provided classloader.
     * @throws IOException
     */
    <T extends Serializable> T unmarshall(final byte[] that, final Class<T> clazz, final ClassLoader loader) throws ClassNotFoundException, IOException;
}
