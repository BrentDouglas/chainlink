package io.machinecode.chainlink.jsl.groovy

import io.machinecode.chainlink.core.jsl.fluent.FluentJob
import io.machinecode.chainlink.jsl.groovy.execution.GroovyDecision
import io.machinecode.chainlink.jsl.groovy.execution.GroovyFlow
import io.machinecode.chainlink.jsl.groovy.execution.GroovySplit
import io.machinecode.chainlink.jsl.groovy.execution.GroovyStep

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
class GroovyJob {

    final FluentJob _value = new FluentJob();

    def id(final String id) {
        _value.id = id
    }

    def version(final String version) {
        _value.version = version
    }

    def restartable(final String restartable) {
        _value.restartable = restartable
    }

    def props(final Closure cl) {
        def that = new GroovyProperties()
        _value.properties = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def listeners(final Closure cl) {
        def that = new GroovyListeners()
        _value.listeners = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    // Executions

    def step(final Closure cl) {
        def that = new GroovyStep()
        _value.executions.add that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def flow(final Closure cl) {
        def that = new GroovyFlow()
        _value.executions.add that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def split(final Closure cl) {
        def that = new GroovySplit()
        _value.executions.add that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def decision(final Closure cl) {
        def that = new GroovyDecision()
        _value.executions.add that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}