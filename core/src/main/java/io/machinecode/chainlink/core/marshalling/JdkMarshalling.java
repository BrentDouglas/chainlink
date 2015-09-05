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
package io.machinecode.chainlink.core.marshalling;

import io.machinecode.chainlink.spi.marshalling.Marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JdkMarshalling implements Marshalling {

    @Override
    public byte[] marshallLong(final long that) throws IOException {
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (final ObjectOutputStream marshaller = new ObjectOutputStream(stream)) {
            marshaller.writeLong(that);
            marshaller.flush();
        }
        return stream.toByteArray();
    }

    @Override
    public byte[] marshall(final Serializable that) throws IOException {
        if (that == null) {
            return null;
        }
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (final ObjectOutputStream marshaller = new ObjectOutputStream(stream)) {
            marshaller.writeObject(that);
            marshaller.flush();
        }
        return stream.toByteArray();
    }

    @Override
    public Serializable unmarshall(final byte[] that, final ClassLoader loader) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        return unmarshall(that, Serializable.class, loader);
    }

    @Override
    public long unmarshallLong(final byte[] that, final ClassLoader loader) throws ClassNotFoundException, IOException {
        if (that == null) {
            throw new IllegalArgumentException(); //TODO Message
        }
        try (final ObjectInputStream unmarshaller = new ClassLoaderObjectInputStream(loader, new ByteArrayInputStream(that))) {
            return unmarshaller.readLong();
        }
    }

    @Override
    public <T extends Serializable> T unmarshall(final byte[] that, final Class<T> clazz, final ClassLoader loader) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        try (final ObjectInputStream unmarshaller = new ClassLoaderObjectInputStream(loader, new ByteArrayInputStream(that))) {
            final Object ret = unmarshaller.readObject();
            return clazz.cast(ret);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T clone(final T that) throws Exception {
        if (that == null) {
            return null;
        }
        if (that instanceof Serializable) {
            return (T) unmarshall(marshall((Serializable)that), that.getClass().getClassLoader());
        } else if (that instanceof Cloneable) {
            final InvokeCloneMethod<T> invoke = new InvokeCloneMethod<>(that);
            final T clone = AccessController.doPrivileged(invoke);
            if (invoke.exception != null) {
                throw invoke.exception;
            }
            return clone;
        }
        throw new IllegalStateException(); //TODO Message
    }

    private static class InvokeCloneMethod<T> implements PrivilegedAction<T> {
        final T that;
        Exception exception;

        private InvokeCloneMethod(final T that) {
            this.that = that;
        }

        @Override
        public T run() {
            final Method method;
            try {
                method = Object.class.getDeclaredMethod("clone");
            } catch (final NoSuchMethodException e) {
                exception = new IllegalStateException(e);
                return null;
            }
            final boolean accessible = method.isAccessible();
            try {
                method.setAccessible(true);
                return (T)method.invoke(that);
            } catch (final IllegalAccessException e) {
                exception = new InvalidClassException("Can't access #clone() on " + that.getClass().getName());
                return null;
            } catch (final InvocationTargetException e) {
                exception = new InvalidObjectException("Failed calling #clone() on " + that);
                return null;
            } finally {
                method.setAccessible(accessible);
            }
        }
    }

    protected static class ClassLoaderObjectInputStream extends ObjectInputStream {

        private static final HashMap<String, Class<?>> primitives = new HashMap<>(8, 1.0F);

        static {
            primitives.put("boolean", boolean.class);
            primitives.put("byte", byte.class);
            primitives.put("char", char.class);
            primitives.put("short", short.class);
            primitives.put("int", int.class);
            primitives.put("long", long.class);
            primitives.put("float", float.class);
            primitives.put("double", double.class);
            primitives.put("void", void.class);
        }

        final ClassLoader loader;

        public ClassLoaderObjectInputStream(final ClassLoader loader, final InputStream in) throws IOException {
            super(in);
            this.loader = loader;
        }

        @Override
        protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
            String name = desc.getName();
            int pos = 0;
            if (name.startsWith("[", pos)) {
                do {
                    ++pos;
                } while (name.startsWith("[", pos));

                final int len = name.length() - pos;
                switch (len) {
                    case 0:
                        throw new ClassNotFoundException("Malformed class name: " + desc.getName());
                    case 1:
                        switch (name.charAt(pos)) {
                            case 'B': return byte[].class;
                            case 'C': return char[].class;
                            case 'D': return double[].class;
                            case 'F': return float[].class;
                            case 'I': return int[].class;
                            case 'J': return long[].class;
                            case 'S': return short[].class;
                            case 'Z': return boolean[].class;
                            default:
                                throw new ClassNotFoundException("Malformed class name: " + desc.getName());
                        }
                    case 2:
                        throw new ClassNotFoundException("Malformed class name: " + desc.getName());
                    default:
                        final Class<?> clazz = loader.loadClass(name.substring(pos + 1, name.length()-1));
                        if (pos == 1) {
                            return Array.newInstance(clazz, pos).getClass();
                        } else {
                            final int[] dims = new int[pos];
                            Arrays.fill(dims, 0);
                            return Array.newInstance(clazz, dims).getClass();
                        }
                }
            } else {
                final Class<?> ret = primitives.get(name);
                if (ret != null) {
                    return ret;
                }
                return loader.loadClass(name);
            }
        }
    }
}
