package io.machinecode.nock.jsl.fluent.transition;

import io.machinecode.nock.spi.element.transition.End;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentEnd extends FluentTerminatingTransition<FluentEnd> implements End {

    @Override
    public FluentEnd copy() {
        return copy(new FluentEnd());
    }
}
