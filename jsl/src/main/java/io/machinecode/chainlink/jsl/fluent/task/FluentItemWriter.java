package io.machinecode.chainlink.jsl.fluent.task;

import io.machinecode.chainlink.spi.element.task.ItemWriter;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentItemWriter extends FluentPropertyReference<FluentItemWriter> implements ItemWriter {
    @Override
    public FluentItemWriter copy() {
        return copy(new FluentItemWriter());
    }
}
