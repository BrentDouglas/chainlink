package io.machinecode.chainlink.spi.context;

import javax.batch.runtime.Metric;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface MutableMetric extends Metric {

    void setValue(final long value);

    void increment();

    void increment(final long value);
}
