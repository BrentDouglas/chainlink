package io.machinecode.chainlink.jsl.fluent.task;

import io.machinecode.chainlink.spi.element.task.ItemWriter;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FluentItemWriter extends FluentPropertyReference<FluentItemWriter> implements ItemWriter {
    @Override
    public FluentItemWriter copy() {
        return copy(new FluentItemWriter());
    }
}
