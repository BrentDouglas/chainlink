package io.machinecode.chainlink.core.jsl.fluent.partition;

import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.spi.element.partition.Mapper;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentMapper extends FluentPropertyReference<FluentMapper> implements FluentStrategy<FluentMapper>, Mapper {
    @Override
    public FluentMapper copy() {
        return copy(new FluentMapper());
    }
}
