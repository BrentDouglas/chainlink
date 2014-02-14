package io.machinecode.nock.core.impl;

import io.machinecode.nock.spi.context.MutableMetric;

import javax.batch.runtime.Metric;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MutableMetricImpl implements MutableMetric {

    private final MetricType type;
    private long value;

    public MutableMetricImpl(final MetricType type) {
        this.type = type;
    }

    public MutableMetricImpl(final Metric metric) {
        this.type = metric.getType();
        this.value = metric.getValue();
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

    @Override
    public void increment(final long value) {
        this.value += value;
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
