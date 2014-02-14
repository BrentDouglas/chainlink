package io.machinecode.nock.spi.context;

import javax.batch.runtime.Metric;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface MutableMetric extends Metric {

    void increment();

    void increment(final long value);
}
