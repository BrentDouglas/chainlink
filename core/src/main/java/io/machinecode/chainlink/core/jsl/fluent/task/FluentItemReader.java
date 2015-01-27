package io.machinecode.chainlink.core.jsl.fluent.task;

import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.spi.jsl.task.ItemReader;

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
