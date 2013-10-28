package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.context.MutableMetric;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MutableMetricImpl implements MutableMetric {

    private final MetricType type;
    private long value;

    public MutableMetricImpl(final MetricType type) {
        this.type = type;
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
    public void increment() {
        ++value;
    }
}
