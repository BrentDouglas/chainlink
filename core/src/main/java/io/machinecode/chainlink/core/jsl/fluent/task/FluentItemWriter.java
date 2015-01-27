package io.machinecode.chainlink.core.jsl.fluent.task;

import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.spi.jsl.task.ItemWriter;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentItemWriter extends FluentPropertyReference<FluentItemWriter> implements ItemWriter {
    @Override
    public FluentItemWriter copy() {
        return copy(new FluentItemWriter());
    }
}
