package io.machinecode.chainlink.jsl.groovy.partition

import io.machinecode.chainlink.core.jsl.fluent.partition.FluentPartition

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GroovyPartition {

    final FluentPartition _value = new FluentPartition();

    def plan(final Closure cl) {
        def that = new GroovyPlan()
        _value.strategy = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def mapper(final Closure cl) {
        def that = new GroovyMapper()
        _value.strategy = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def analyser(final Closure cl) {
        def that = new GroovyAnalyser()
        _value.analyzer = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def reducer(final Closure cl) {
        def that = new GroovyReducer()
        _value.reducer = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def collector(final Closure cl) {
        def that = new GroovyCollector()
        _value.collector = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
