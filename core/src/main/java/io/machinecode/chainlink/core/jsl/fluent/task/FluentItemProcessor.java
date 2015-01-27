package io.machinecode.chainlink.core.jsl.fluent.task;

import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.spi.jsl.task.ItemProcessor;

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
