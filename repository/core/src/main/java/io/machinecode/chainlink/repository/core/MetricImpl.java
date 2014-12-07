package io.machinecode.chainlink.repository.core;

import javax.batch.runtime.Metric;
import java.io.Serializable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
