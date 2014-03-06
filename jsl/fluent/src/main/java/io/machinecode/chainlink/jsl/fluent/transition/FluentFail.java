package io.machinecode.chainlink.jsl.fluent.transition;

import io.machinecode.chainlink.spi.element.transition.Fail;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentFail extends FluentTerminatingTransition<FluentFail> implements Fail {

    @Override
    public FluentFail copy() {
        return copy(new FluentFail());
    }
}
