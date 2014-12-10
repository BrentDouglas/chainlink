package io.machinecode.chainlink.marshalling.jboss;

import io.machinecode.chainlink.spi.marshalling.Marshalling;
import org.jboss.marshalling.ByteBufferInput;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.OutputStreamByteOutput;
import org.jboss.marshalling.cloner.ClonerConfiguration;
import org.jboss.marshalling.cloner.ObjectCloners;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JbossMarshalling implements Marshalling {

    final MarshallerFactory marshallerFactory;
    final MarshallingConfiguration marshallingConfiguration;
    final ClonerConfiguration clonerConfiguration;

    public JbossMarshalling(final MarshallerFactory marshallerFactory, final MarshallingConfiguration marshallingConfiguration,
                            final ClonerConfiguration clonerConfiguration) {
        this.marshallerFactory = marshallerFactory;
        this.marshallingConfiguration = marshallingConfiguration;
        this.clonerConfiguration = clonerConfiguration;
    }

    @Override
    public byte[] marshall(final Serializable that) throws IOException {
        if (that == null) {
            return null;
        }
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final org.jboss.marshalling.Marshaller marshaller = marshallerFactory.createMarshaller(marshallingConfiguration);
        marshaller.start(new OutputStreamByteOutput(stream));
        marshaller.writeObject(that);
        marshaller.finish();
        marshaller.flush();
        marshaller.close();
        return stream.toByteArray();
    }

    @Override
    public byte[] marshall(final Serializable... that) throws IOException {
        if (that == null) {
            return null;
        }
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final org.jboss.marshalling.Marshaller marshaller = marshallerFactory.createMarshaller(marshallingConfiguration);
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
        //TODO Read out of loader
        final org.jboss.marshalling.Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(marshallingConfiguration);
        unmarshaller.start(new ByteBufferInput(ByteBuffer.wrap(that)));
        final Object ret = unmarshaller.readObject();
        unmarshaller.finish();
        unmarshaller.close();
        return clazz.cast(ret);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T clone(final T that) throws ClassNotFoundException, IOException {
        if (that == null) {
            return null;
        }
        if (that instanceof Serializable) {
            return (T)ObjectCloners.getSerializingObjectClonerFactory().createCloner(clonerConfiguration).clone(that);
        } else if (that instanceof Cloneable) {
            return (T)ObjectCloners.getCloneableObjectClonerFactory().createCloner(clonerConfiguration).clone(that);
        }
        throw new IllegalStateException(); //TODO Message
    }
}
