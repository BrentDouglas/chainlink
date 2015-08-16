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
package io.machinecode.chainlink.repository.jpa;

import io.machinecode.chainlink.core.context.MutableMetric;

import javax.batch.runtime.Metric;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Entity
@Table(name = "metric")
public class JpaMetric implements MutableMetric, Serializable {
    private static final long serialVersionUID = 1L;

    private long id;
    private MetricType type;
    private long value;

    public JpaMetric() {
    }

    public JpaMetric(final MetricType type) {
        this.type = type;
        this.value = 0;
    }

    public JpaMetric(final Metric metric) {
        this.type = metric.getType();
        this.value = metric.getValue();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    @Override
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    public MetricType getType() {
        return type;
    }

    public void setType(final MetricType type) {
        this.type = type;
    }

    @Override
    @Column(name = "value", nullable = false)
    public long getValue() {
        return value;
    }

    @Override
    public void setValue(final long value) {
        this.value = value;
    }

    public static Map<MetricType, JpaMetric> empty() {
        final Map<MetricType, JpaMetric> mets = new HashMap<>(MetricType.values().length);
        for (final MetricType type : MetricType.values()) {
            mets.put(type, new JpaMetric(type));
        }
        return mets;
    }

    public static Map<MetricType, JpaMetric> copy(final Map<MetricType, JpaMetric> list) {
        final Map<MetricType, JpaMetric> ret = new HashMap<>(list.size());
        for (final Map.Entry<MetricType, JpaMetric> entry : list.entrySet()) {
            ret.put(entry.getKey(), new JpaMetric(entry.getValue()));
        }
        return ret;
    }

    public static JpaMetric[] copy(final Metric[] metrics) {
        if (metrics == null) {
            return null;
        }
        final JpaMetric[] mets = new JpaMetric[metrics.length];
        for (int i = 0; i < metrics.length; ++i) {
            mets[i] = new JpaMetric(metrics[i]);
        }
        return mets;
    }

    @Override
    public void increment() {
        ++this.value;
    }

    @Override
    public void increment(final long value) {
        this.value += value;
    }
}
