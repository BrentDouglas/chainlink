package io.machinecode.chainlink.jsl.groovy.transition

import io.machinecode.chainlink.jsl.fluent.transition.FluentFail

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
public class GroovyFail {

    final FluentFail _value = new FluentFail();

    def on(final String on) {
        _value.on = on
    }

    def exitStatus(final String exitStatus) {
        _value.exitStatus = exitStatus
    }
}
