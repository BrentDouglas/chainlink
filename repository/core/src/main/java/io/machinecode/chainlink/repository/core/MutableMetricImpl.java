package io.machinecode.chainlink.repository.core;

import io.machinecode.chainlink.spi.context.MutableMetric;

import javax.batch.runtime.Metric;
import java.io.Serializable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class MutableMetricImpl implements MutableMetric, Serializable {

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
