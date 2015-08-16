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

import io.machinecode.chainlink.core.context.MutableMetric;

import javax.batch.runtime.Metric;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class MutableMetricImpl implements MutableMetric, Serializable {
    private static final long serialVersionUID = 1L;

    private final MetricType type;
    private long value;

    public MutableMetricImpl(final MetricType type, final long value) {
        this.type = type;
        this.value = value;
    }

    public MutableMetricImpl(final MetricType type) {
        this(type, 0);
    }

    public MutableMetricImpl(final Metric metric) {
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

    @Override
    public void setValue(final long value) {
        this.value = value;
    }

    @Override
    public void increment() {
        ++value;
    }

    @Override
    public void increment(final long value) {
        this.value += value;
    }

    public static MutableMetricImpl[] empty() {
        final MutableMetricImpl[] mets = new MutableMetricImpl[MetricType.values().length];
        for (int i = 0; i < MetricType.values().length; ++i) {
            mets[i] = new MutableMetricImpl(MetricType.values()[i]);
        }
        return mets;
    }

    public static MutableMetricImpl[] copy(final Metric[] metrics) {
        final MetricType[] values = MetricType.values();
        final MutableMetricImpl[] mets = new MutableMetricImpl[values.length];
        if (metrics == null) {
            for (int i = 0; i < values.length; ++i) {
                mets[i] = new MutableMetricImpl(values[i]);
            }
        } else {
            for (int i = 0; i < metrics.length; ++i) {
                mets[i] = new MutableMetricImpl(metrics[i]);
            }
        }
        return mets;
    }
}
