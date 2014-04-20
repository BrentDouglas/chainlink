package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.serialization.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JdkSerializer implements Serializer {

    @Override
    public byte[] bytes(final Serializable that) throws IOException {
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
    public byte[] bytes(final Serializable... that) throws IOException {
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
    public Serializable read(final byte[] that) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        return read(that, Serializable.class);
    }

    @Override
    public <T> T read(final byte[] that, final Class<T> clazz) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        ObjectInputStream unmarshaller = null;
        try {
            unmarshaller = new ObjectInputStream(new ByteArrayInputStream(that));
            final Object ret = unmarshaller.readObject();
            return clazz.cast(ret);
        } finally {
            if (unmarshaller != null) {
                unmarshaller.close();
            }
        }
    }

    @Override
    public <T extends Serializable> T clone(final T that) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        return (T) read(bytes(that));
    }
}
