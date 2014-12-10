package io.machinecode.chainlink.jsl.fluent.task;

import io.machinecode.chainlink.spi.element.task.ItemReader;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentItemReader extends FluentPropertyReference<FluentItemReader> implements ItemReader {
    @Override
    public FluentItemReader copy() {
        return copy(new FluentItemReader());
    }
}
