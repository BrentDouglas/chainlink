package io.machinecode.chainlink.jsl.groovy

import io.machinecode.chainlink.jsl.fluent.FluentJob

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
class Dsl {

    def static FluentJob job(final Closure cl) {
        final GroovyJob that = new GroovyJob();
        def code = cl.rehydrate(that, that, that)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
        that._value
    }
}