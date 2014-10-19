package io.machinecode.chainlink.jsl.groovy

import io.machinecode.chainlink.jsl.fluent.FluentListeners;

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
public class GroovyListeners {

    final FluentListeners _value = new FluentListeners();

    def listener(final Closure cl) {
        def that = new GroovyListener()
        _value.listeners.add that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
