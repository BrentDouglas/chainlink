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
package io.machinecode.chainlink.core.repository;

import javax.batch.runtime.Metric;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MetricImpl implements Metric, Serializable {
    private static final long serialVersionUID = 1L;

    private final MetricType type;
    private final long value;

    public MetricImpl(final MetricType type, final long value) {
        this.type = type;
        this.value = value;
    }

    public MetricImpl(final Metric metric) {
        this(metric.getType(), metric.getValue());
    }

    @Override
    public MetricType getType() {
        return type;
    }

    @Override
    public long getValue() {
        return value;
    }

    public static MetricImpl[] empty() {
        final MetricImpl[] mets = new MetricImpl[MetricType.values().length];
        for (int i = 0; i < MetricType.values().length; ++i) {
            mets[i] = new MetricImpl(MetricType.values()[i], 0);
        }
        return mets;
    }

    public static MetricImpl[] copy(final Metric[] metrics) {
        if (metrics == null) {
            return null;
        }
        final MetricImpl[] mets = new MetricImpl[metrics.length];
        for (int i = 0; i < metrics.length; ++i) {
            mets[i] = new MetricImpl(metrics[i]);
        }
        return mets;
    }
}
