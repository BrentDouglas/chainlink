package io.machinecode.chainlink.jsl.groovy.transition

import io.machinecode.chainlink.jsl.fluent.transition.FluentStop

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
public class GroovyStop {

    final FluentStop _value = new FluentStop();

    def on(final String on) {
        _value.on = on
    }

    def restart(final String restart) {
        _value.restart = restart
    }

    def exitStatus(final String exitStatus) {
        _value.exitStatus = exitStatus
    }
}
