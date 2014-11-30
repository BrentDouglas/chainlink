package io.machinecode.chainlink.jsl.fluent.transition;

import io.machinecode.chainlink.spi.element.transition.End;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FluentEnd extends FluentTerminatingTransition<FluentEnd> implements End {

    @Override
    public FluentEnd copy() {
        return copy(new FluentEnd());
    }
}
