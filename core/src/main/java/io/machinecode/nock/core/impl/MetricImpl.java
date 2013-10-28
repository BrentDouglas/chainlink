package io.machinecode.nock.core.impl;

import javax.batch.runtime.Metric;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MetricImpl implements Metric {

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
}
