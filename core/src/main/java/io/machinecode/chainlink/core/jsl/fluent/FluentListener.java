package io.machinecode.chainlink.core.jsl.fluent;

import io.machinecode.chainlink.spi.jsl.Listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentListener extends FluentPropertyReference<FluentListener> implements Listener {
    @Override
    public FluentListener copy() {
        return copy(new FluentListener());
    }
}
