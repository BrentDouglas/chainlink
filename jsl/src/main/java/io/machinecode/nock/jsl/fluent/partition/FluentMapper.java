package io.machinecode.nock.jsl.fluent.partition;

import io.machinecode.nock.spi.element.partition.Mapper;
import io.machinecode.nock.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentMapper extends FluentPropertyReference<FluentMapper> implements FluentStrategy<FluentMapper>, Mapper {
    @Override
    public FluentMapper copy() {
        return copy(new FluentMapper());
    }
}
