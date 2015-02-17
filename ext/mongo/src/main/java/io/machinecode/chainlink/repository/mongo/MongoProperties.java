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
