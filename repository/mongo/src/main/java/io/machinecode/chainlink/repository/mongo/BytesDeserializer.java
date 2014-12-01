package io.machinecode.chainlink.repository.mongo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.machinecode.chainlink.marshalling.jdk.JdkMarshallingProvider;
import io.machinecode.chainlink.spi.marshalling.Unmarshaller;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class BytesDeserializer extends JsonDeserializer<Serializable> {

    //TODO Work out if we can access the one in the repo
    final Unmarshaller unmarshaller = new JdkMarshallingProvider().getUnmarshaller();

    @Override
    public Serializable deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        try {
            return unmarshaller.unmarshall(parser.getBinaryValue(), Thread.currentThread().getContextClassLoader());
        } catch (final ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}
