package io.machinecode.chainlink.core.jsl.fluent.transition;

import io.machinecode.chainlink.spi.jsl.transition.Fail;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentFail extends FluentTerminatingTransition<FluentFail> implements Fail {

    @Override
    public FluentFail copy() {
        return copy(new FluentFail());
    }
}
