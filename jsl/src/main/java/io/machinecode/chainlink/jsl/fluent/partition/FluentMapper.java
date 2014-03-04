package io.machinecode.chainlink.jsl.fluent.partition;

import io.machinecode.chainlink.spi.element.partition.Mapper;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentMapper extends FluentPropertyReference<FluentMapper> implements FluentStrategy<FluentMapper>, Mapper {
    @Override
    public FluentMapper copy() {
        return copy(new FluentMapper());
    }
}
