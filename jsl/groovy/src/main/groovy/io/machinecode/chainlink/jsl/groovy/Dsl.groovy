package io.machinecode.chainlink.jsl.groovy

import io.machinecode.chainlink.core.jsl.fluent.FluentJob

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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