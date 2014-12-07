package io.machinecode.chainlink.marshalling.jdk;

import io.machinecode.chainlink.spi.marshalling.Cloner;
import io.machinecode.chainlink.spi.marshalling.Marshaller;
import io.machinecode.chainlink.spi.marshalling.MarshallingProvider;
import io.machinecode.chainlink.spi.marshalling.Unmarshaller;

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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JdkMarshallingProvider implements Marshaller, Unmarshaller, Cloner, MarshallingProvider {

    @Override
    public byte[] marshall(final Serializable that) throws IOException {
        if (that == null) {
            return null;
        }
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream marshaller = null;
        try {
            marshaller = new ObjectOutputStream(stream);
            marshaller.writeObject(that);
            marshaller.flush();
        } finally {
            if (marshaller != null) {
                marshaller.close();
            }
        }
        return stream.toByteArray();
    }

    @Override
    public byte[] marshall(final Serializable... that) throws IOException {
        if (that == null) {
            return null;
        }
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream marshaller = null;
        try {
            marshaller = new ObjectOutputStream(stream);
            for (final Serializable value : that) {
                marshaller.writeObject(value);
            }
            marshaller.flush();
        } finally {
            if (marshaller != null) {
                marshaller.close();
            }
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
    public <T extends Serializable> T unmarshall(final byte[] that, final Class<T> clazz, final ClassLoader loader) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        ObjectInputStream unmarshaller = null;
        try {
            unmarshaller = new ClassLoaderObjectInputStream(loader, new ByteArrayInputStream(that));
            final Object ret = unmarshaller.readObject();
            return clazz.cast(ret);
        } finally {
            if (unmarshaller != null) {
                unmarshaller.close();
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T clone(final T that) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        if (that instanceof Serializable) {
            return (T) unmarshall(marshall((Serializable)that), that.getClass().getClassLoader());
        } else if (that instanceof Cloneable) {
            final Method method = AccessController.doPrivileged(new PrivilegedAction<Method>() {
                public Method run() {
                    final Method method;
                    try {
                        method = Object.class.getDeclaredMethod("clone");
                    } catch (final NoSuchMethodException e) {
                        throw new IllegalStateException(e);
                    }
                    method.setAccessible(true);
                    return method;
                }
            });
            try {
                return (T)method.invoke(that);
            } catch (final IllegalAccessException e) {
                throw new InvalidClassException("Can't access #clone() on " + that.getClass().getName());
            } catch (final InvocationTargetException e) {
                throw new InvalidObjectException("Failed calling #clone() on " + that);
            }
        }
        throw new IllegalStateException(); //TODO Message
    }

    @Override
    public Cloner getCloner() {
        return this;
    }

    @Override
    public Marshaller getMarshaller() {
        return this;
    }

    @Override
    public Unmarshaller getUnmarshaller() {
        return this;
    }

    protected static class ClassLoaderObjectInputStream extends ObjectInputStream {

        private static final HashMap<String, Class<?>> primitives = new HashMap<String, Class<?>>(8, 1.0F);

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
                if (len == 0) {
                    throw new ClassNotFoundException("Malformed class name: " + desc.getName());
                }
                switch (name.charAt(0)) {
                    case 'B': return byte.class;
                    case 'C': return char.class;
                    case 'D': return double.class;
                    case 'F': return float.class;
                    case 'I': return int.class;
                    case 'J': return long.class;
                    case 'S': return short.class;
                    case 'Z': return boolean.class;
                    default:
                        if (len < 3) {
                            throw new ClassNotFoundException("Malformed class name: " + desc.getName());
                        }
                        return loader.loadClass(name.substring(pos + 1, name.length()-1));
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
