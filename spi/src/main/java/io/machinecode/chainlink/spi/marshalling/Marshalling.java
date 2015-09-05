/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.spi.marshalling;

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
     * @throws Exception
     */
    <T> T clone(final T that) throws Exception;

    /**
     * <p>Write the value to a byte array.</p>
     *
     * @param that The value to write.
     * @return The serialized data.
     * @throws Exception
     */
    byte[] marshallLong(final long that) throws Exception;

    /**
     * <p>Write the value to a byte array.</p>
     *
     * @param that The value to write.
     * @return The serialized data.
     * @throws Exception
     */
    byte[] marshall(final Serializable that) throws Exception;

    /**
     * <p>Read an object from a byte array.</p>
     *
     * @param that The bytes to read from.
     * @param loader The classloader to load classes from.
     * @return The deserialized object.
     * @throws ClassNotFoundException If the object is of a type that is not available to the provided classloader.
     * @throws Exception
     */
    Serializable unmarshall(final byte[] that, final ClassLoader loader) throws Exception;

    /**
     * <p>Read a long from a byte array.</p>
     *
     * @param that The bytes to read from.
     * @param loader The classloader to load classes from.
     * @return The deserialized long.
     * @throws ClassNotFoundException If the object is of a type that is not available to the provided classloader.
     * @throws Exception
     */
    long unmarshallLong(final byte[] that, final ClassLoader loader) throws Exception;

    /**
     * <p>Read a typed object from a byte array.</p>
     *
     * @param that The bytes to read from.
     * @param clazz The class of the object to load.
     * @param loader The classloader to load classes from.
     * @param <T> The object type.
     * @return The deserialized object.
     * @throws ClassNotFoundException If the object is of a type that is not available to the provided classloader.
     * @throws Exception
     */
    <T extends Serializable> T unmarshall(final byte[] that, final Class<T> clazz, final ClassLoader loader) throws Exception;
}
