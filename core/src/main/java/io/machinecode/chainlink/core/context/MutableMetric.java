package io.machinecode.chainlink.core.context;

import javax.batch.runtime.Metric;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableMetric extends Metric {

    void setValue(final long value);

    void increment();

    void increment(final long value);
}
