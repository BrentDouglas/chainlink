package io.machinecode.nock.jsl.fluent.transition;

import io.machinecode.nock.spi.element.transition.Fail;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentFail extends FluentTerminatingTransition<FluentFail> implements Fail {

    @Override
    public FluentFail copy() {
        return copy(new FluentFail());
    }
}
