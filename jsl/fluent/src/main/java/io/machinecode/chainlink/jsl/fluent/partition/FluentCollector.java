package io.machinecode.chainlink.jsl.fluent.partition;

import io.machinecode.chainlink.spi.element.partition.Collector;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FluentCollector extends FluentPropertyReference<FluentCollector> implements Collector {
    @Override
    public FluentCollector copy() {
        return copy(new FluentCollector());
    }
}
