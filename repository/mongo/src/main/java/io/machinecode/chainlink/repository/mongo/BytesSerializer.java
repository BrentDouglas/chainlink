package io.machinecode.chainlink.repository.mongo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.machinecode.chainlink.marshalling.jdk.JdkMarshalling;
import io.machinecode.chainlink.spi.marshalling.Marshalling;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BytesSerializer extends JsonSerializer<Serializable> {

    //TODO Work out if we can access the one in the repo
    final Marshalling marshalling = new JdkMarshalling();

    @Override
    public void serialize(final Serializable value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {
        generator.writeBinary(marshalling.marshall(value));
    }
}
