package io.machinecode.chainlink.jsl.fluent.partition;

import io.machinecode.chainlink.spi.element.partition.Reducer;
import io.machinecode.chainlink.jsl.fluent.FluentPropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentReducer extends FluentPropertyReference<FluentReducer> implements Reducer {
    @Override
    public FluentReducer copy() {
        return copy(new FluentReducer());
    }
}
