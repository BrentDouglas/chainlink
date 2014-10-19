package io.machinecode.chainlink.jsl.groovy.transition

import io.machinecode.chainlink.jsl.fluent.transition.FluentEnd

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
public class GroovyEnd {

    final FluentEnd _value = new FluentEnd();

    def on(final String on) {
        _value.on = on
    }

    def exitStatus(final String exitStatus) {
        _value.exitStatus = exitStatus
    }
}
