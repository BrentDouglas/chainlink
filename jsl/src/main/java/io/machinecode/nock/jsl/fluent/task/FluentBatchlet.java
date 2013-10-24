package io.machinecode.nock.jsl.fluent.task;

import io.machinecode.nock.spi.element.task.Batchlet;
import io.machinecode.nock.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentBatchlet extends FluentPropertyReference<FluentBatchlet> implements FluentTask<FluentBatchlet>, Batchlet {

    @Override
    public FluentBatchlet copy() {
        return copy(new FluentBatchlet());
    }
}
