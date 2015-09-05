/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.batch.runtime.Metric;
import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@JsonAutoDetect(
        getterVisibility = NONE,
        isGetterVisibility = NONE,
        setterVisibility = NONE,
        fieldVisibility = NONE,
        creatorVisibility = NONE
)
public class MongoMetric implements Metric, Serializable {
    private static final long serialVersionUID = 1L;

    private final MetricType type;
    private final long value;

    @JsonCreator
    public MongoMetric(@JsonProperty(Fields.TYPE) final MetricType type, @JsonProperty(Fields.VALUE) final long value) {
        this.type = type;
        this.value = value;
    }

    public MongoMetric(final Metric metric) {
        this(metric.getType(), metric.getValue());
    }

    @Override
    @JsonProperty(Fields.TYPE)
    public MetricType getType() {
        return type;
    }

    @Override
    @JsonProperty(Fields.VALUE)
    public long getValue() {
        return value;
    }

    public static MongoMetric[] empty() {
        final MongoMetric[] mets = new MongoMetric[MetricType.values().length];
        for (int i = 0; i < MetricType.values().length; ++i) {
            mets[i] = new MongoMetric(MetricType.values()[i], 0);
        }
        return mets;
    }

    public static MongoMetric[] copy(final Metric[] metrics) {
        if (metrics == null) {
            return null;
        }
        final MongoMetric[] mets = new MongoMetric[metrics.length];
        for (int i = 0; i < metrics.length; ++i) {
            mets[i] = new MongoMetric(metrics[i]);
        }
        return mets;
    }
}
