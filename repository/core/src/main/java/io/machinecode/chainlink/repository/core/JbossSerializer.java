package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.serialization.Serializer;
import org.jboss.marshalling.ByteBufferInput;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.OutputStreamByteOutput;
import org.jboss.marshalling.Unmarshaller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * //TODO Move this to separate JBoss module
 *
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JbossSerializer implements Serializer {

    final MarshallerFactory marshallerFactory;
    final MarshallingConfiguration configuration;

    public JbossSerializer(final MarshallerFactory marshallerFactory, final MarshallingConfiguration configuration) {
        this.marshallerFactory = marshallerFactory;
        this.configuration = configuration;
    }

    @Override
    public byte[] bytes(final Serializable that) throws IOException {
        if (that == null) {
            return null;
        }
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        marshaller.start(new OutputStreamByteOutput(stream));
        marshaller.writeObject(that);
        marshaller.finish();
        marshaller.flush();
        marshaller.close();
        return stream.toByteArray();
    }

    @Override
    public byte[] bytes(final Serializable... that) throws IOException {
        if (that == null) {
            return null;
        }
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
        marshaller.start(new OutputStreamByteOutput(stream));
        for (final Serializable value : that) {
            marshaller.writeObject(value);
        }
        marshaller.finish();
        marshaller.flush();
        marshaller.close();
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
        final Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
        unmarshaller.start(new ByteBufferInput(ByteBuffer.wrap(that)));
        final Object ret = unmarshaller.readObject();
        unmarshaller.finish();
        unmarshaller.close();
        return clazz.cast(ret);
    }

    @Override
    public <T extends Serializable> T clone(final T that) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        return (T) read(bytes(that));
    }
}
