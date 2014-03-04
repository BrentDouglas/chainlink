package io.machinecode.chainlink.jsl.fluent.task;

import io.machinecode.chainlink.spi.element.task.ItemReader;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentItemReader extends FluentPropertyReference<FluentItemReader> implements ItemReader {
    @Override
    public FluentItemReader copy() {
        return copy(new FluentItemReader());
    }
}
