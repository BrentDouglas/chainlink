package io.machinecode.chainlink.jsl.groovy.execution

import io.machinecode.chainlink.core.jsl.fluent.execution.FluentFlow
import io.machinecode.chainlink.jsl.groovy.transition.GroovyEnd
import io.machinecode.chainlink.jsl.groovy.transition.GroovyFail
import io.machinecode.chainlink.jsl.groovy.transition.GroovyNext
import io.machinecode.chainlink.jsl.groovy.transition.GroovyStop

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
class GroovyFlow {

    final FluentFlow _value = new FluentFlow();

    def id(final String id) {
        _value.id = id
    }

    def next(final String next) {
        _value.next = next
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

    // Transitions

    def end(final Closure cl) {
        def that = new GroovyEnd()
        _value.transitions.add that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def fail(final Closure cl) {
        def that = new GroovyFail()
        _value.transitions.add that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def next(final Closure cl) {
        def that = new GroovyNext()
        _value.transitions.add that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def stop(final Closure cl) {
        def that = new GroovyStop()
        _value.transitions.add that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
