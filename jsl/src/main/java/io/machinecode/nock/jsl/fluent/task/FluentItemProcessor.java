package io.machinecode.nock.jsl.fluent.task;

import io.machinecode.nock.spi.element.task.ItemProcessor;
import io.machinecode.nock.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentItemProcessor extends FluentPropertyReference<FluentItemProcessor> implements ItemProcessor {
    @Override
    public FluentItemProcessor copy() {
        return copy(new FluentItemProcessor());
    }
}
