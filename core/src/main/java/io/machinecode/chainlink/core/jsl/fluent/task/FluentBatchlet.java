package io.machinecode.chainlink.core.jsl.fluent.task;

import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.spi.jsl.task.Batchlet;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentBatchlet extends FluentPropertyReference<FluentBatchlet> implements FluentTask<FluentBatchlet>, Batchlet {

    @Override
    public FluentBatchlet copy() {
        return copy(new FluentBatchlet());
    }
}
