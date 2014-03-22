package io.machinecode.chainlink.repository.core;

import org.jboss.marshalling.ByteBufferInput;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.OutputStreamByteOutput;
import org.jboss.marshalling.Unmarshaller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class Serializer {

    public enum Type { JDK, JBOSS }

    final Type type;
    final MarshallerFactory marshallerFactory;
    final MarshallingConfiguration configuration;

    public Serializer(final Type type, final MarshallerFactory marshallerFactory, final MarshallingConfiguration configuration) {
        this.type = type;
        this.marshallerFactory = marshallerFactory;
        this.configuration = configuration;
    }

    public byte[] bytes(final Serializable... that) throws IOException {
        if (that == null) {
            return null;
        }
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (type == Type.JBOSS) {
        final Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
            marshaller.start(new OutputStreamByteOutput(stream));
            for (final Serializable value : that) {
                marshaller.writeObject(value);
            }
            marshaller.finish();
            marshaller.flush();
            marshaller.close();
        } else {
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
        }
        return stream.toByteArray();
    }

    public Serializable read(final byte[] that) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        return read(that, Serializable.class);
    }

    public <T> T read(final byte[] that, final Class<T> clazz) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        if (type == Type.JBOSS) {
            final Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
            unmarshaller.start(new ByteBufferInput(ByteBuffer.wrap(that)));
            final Object ret = unmarshaller.readObject();
            unmarshaller.finish();
            unmarshaller.close();
            return clazz.cast(ret);
        } else {
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
    }

    public  <T extends Serializable> T clone(final T that) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        return (T) read(bytes(that));
    }
}
