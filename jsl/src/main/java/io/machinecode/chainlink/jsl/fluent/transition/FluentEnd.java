package io.machinecode.chainlink.jsl.fluent.transition;

import io.machinecode.chainlink.spi.element.transition.End;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentEnd extends FluentTerminatingTransition<FluentEnd> implements End {

    @Override
    public FluentEnd copy() {
        return copy(new FluentEnd());
    }
}
