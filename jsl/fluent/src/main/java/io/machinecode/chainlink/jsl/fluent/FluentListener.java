package io.machinecode.chainlink.jsl.fluent;

import io.machinecode.chainlink.spi.element.Listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentListener extends FluentPropertyReference<FluentListener> implements Listener {
    @Override
    public FluentListener copy() {
        return copy(new FluentListener());
    }
}