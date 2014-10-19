package io.machinecode.chainlink.jsl.groovy.execution

import io.machinecode.chainlink.jsl.fluent.execution.FluentDecision
import io.machinecode.chainlink.jsl.groovy.transition.GroovyEnd
import io.machinecode.chainlink.jsl.groovy.transition.GroovyFail
import io.machinecode.chainlink.jsl.groovy.transition.GroovyNext
import io.machinecode.chainlink.jsl.groovy.transition.GroovyStop;

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
public class GroovyDecision {

    final FluentDecision _value = new FluentDecision();

    def id(final String id) {
        _value.id = id
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
