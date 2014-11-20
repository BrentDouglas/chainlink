package io.machinecode.chainlink.repository.mongo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.machinecode.chainlink.marshalling.jdk.JdkMarshallingProvider;
import io.machinecode.chainlink.spi.marshalling.Marshaller;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BytesSerializer extends JsonSerializer<Serializable> {

    //TODO Work out if we can access the one in the repo
    final Marshaller marshaller = new JdkMarshallingProvider().getMarshaller();

    @Override
    public void serialize(final Serializable value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        generator.writeBinary(marshaller.marshall(value));
    }
}
