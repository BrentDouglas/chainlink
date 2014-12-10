package io.machinecode.chainlink.jsl.fluent.partition;

import io.machinecode.chainlink.spi.element.partition.Reducer;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentReducer extends FluentPropertyReference<FluentReducer> implements Reducer {
    @Override
    public FluentReducer copy() {
        return copy(new FluentReducer());
    }
}
