package io.machinecode.chainlink.repository.mongo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MongoProperty {
    final String key;
    final String value;

    @JsonCreator
    public MongoProperty(@JsonProperty(Fields.K) final String key, @JsonProperty(Fields.V) final String value) {
        this.key = key;
        this.value = value;
    }

    @JsonProperty(Fields.K)
    public String getKey() {
        return key;
    }

    @JsonProperty(Fields.V)
    public String getValue() {
        return value;
    }
}
