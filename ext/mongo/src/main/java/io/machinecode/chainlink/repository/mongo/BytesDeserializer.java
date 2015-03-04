package io.machinecode.chainlink.repository.mongo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.machinecode.chainlink.core.marshalling.JdkMarshalling;
import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.spi.marshalling.Marshalling;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BytesDeserializer extends JsonDeserializer<Serializable> {

    //TODO Work out if we can access the one in the repo
    final Marshalling unmarshaller = new JdkMarshalling();

    @Override
    public Serializable deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
        try {
            return unmarshaller.unmarshall(parser.getBinaryValue(), Tccl.get());
        } catch (final IOException e) {
            throw e;
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }
}
