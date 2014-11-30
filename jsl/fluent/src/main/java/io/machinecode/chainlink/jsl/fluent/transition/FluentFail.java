package io.machinecode.chainlink.jsl.fluent.transition;

import io.machinecode.chainlink.spi.element.transition.Fail;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class FluentFail extends FluentTerminatingTransition<FluentFail> implements Fail {

    @Override
    public FluentFail copy() {
        return copy(new FluentFail());
    }
}
