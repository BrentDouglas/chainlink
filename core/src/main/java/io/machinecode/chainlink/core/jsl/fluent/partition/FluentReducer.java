package io.machinecode.chainlink.core.jsl.fluent.partition;

import io.machinecode.chainlink.core.jsl.fluent.FluentPropertyReference;
import io.machinecode.chainlink.spi.jsl.partition.Reducer;

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
