/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
