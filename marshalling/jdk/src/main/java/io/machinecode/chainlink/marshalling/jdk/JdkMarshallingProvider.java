package io.machinecode.chainlink.marshalling.jdk;

import io.machinecode.chainlink.spi.marshalling.Cloner;
import io.machinecode.chainlink.spi.marshalling.Marshaller;
import io.machinecode.chainlink.spi.marshalling.MarshallingProvider;
import io.machinecode.chainlink.spi.marshalling.Unmarshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
            unmarshaller = new ObjectInputStream(new ByteArrayInputStream(that)) {
                @Override
                protected Class<?> resolveClass(final ObjectStreamClass desc) throws IOException, ClassNotFoundException {
                    return loader.loadClass(desc.getName());
                }
            };
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
}
