package io.machinecode.chainlink.jsl.groovy.transition

import io.machinecode.chainlink.jsl.fluent.transition.FluentNext

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
public class GroovyNext {

    final FluentNext _value = new FluentNext();

    def on(final String on) {
        _value.on = on
    }

    def to(final String to) {
        _value.to = to
    }
}
