package io.machinecode.chainlink.jsl.fluent.partition;

import io.machinecode.chainlink.spi.element.partition.Collector;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentCollector extends FluentPropertyReference<FluentCollector> implements Collector {
    @Override
    public FluentCollector copy() {
        return copy(new FluentCollector());
    }
}
