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
