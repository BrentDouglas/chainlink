package io.machinecode.chainlink.spi.context;

import javax.batch.runtime.Metric;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface MutableMetric extends Metric {

    void setValue(final long value);

    void increment();

    void increment(final long value);
}
