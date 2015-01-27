package io.machinecode.chainlink.core.jsl.fluent.partition;

import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.spi.jsl.partition.Collector;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentCollector extends FluentPropertyReference<FluentCollector> implements Collector {
    @Override
    public FluentCollector copy() {
        return copy(new FluentCollector());
    }
}
