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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MongoProperties extends ArrayList<MongoProperty> {
    private static final long serialVersionUID = 1L;

    public MongoProperties(final int initialCapacity) {
        super(initialCapacity);
    }

    public MongoProperties() {
        super();
    }

    public MongoProperties(final Collection<? extends MongoProperty> c) {
        super(c);
    }

    public MongoProperties(final Properties properties) {
        super(properties.size());
        for (final String key : properties.stringPropertyNames()) {
            add(new MongoProperty(key, properties.getProperty(key)));
        }
    }

    public Properties toProperties() {
        final Properties properties = new Properties();
        for (final MongoProperty that : this) {
            properties.put(that.getKey(), that.getValue());
        }
        return properties;
    }
}
