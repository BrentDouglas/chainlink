package io.machinecode.chainlink.jsl.fluent.task;

import io.machinecode.chainlink.spi.element.task.ItemProcessor;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentItemProcessor extends FluentPropertyReference<FluentItemProcessor> implements ItemProcessor {
    @Override
    public FluentItemProcessor copy() {
        return copy(new FluentItemProcessor());
    }
}
