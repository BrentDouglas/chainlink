package io.machinecode.chainlink.jsl.groovy.execution

import io.machinecode.chainlink.jsl.fluent.execution.FluentStep
import io.machinecode.chainlink.jsl.groovy.GroovyListeners
import io.machinecode.chainlink.jsl.groovy.GroovyProperties
import io.machinecode.chainlink.jsl.groovy.partition.GroovyPartition
import io.machinecode.chainlink.jsl.groovy.task.GroovyBatchlet
import io.machinecode.chainlink.jsl.groovy.task.GroovyChunk
import io.machinecode.chainlink.jsl.groovy.transition.GroovyEnd
import io.machinecode.chainlink.jsl.groovy.transition.GroovyFail
import io.machinecode.chainlink.jsl.groovy.transition.GroovyNext
import io.machinecode.chainlink.jsl.groovy.transition.GroovyStop

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
public class GroovyStep {

    final FluentStep _value = new FluentStep();

    def id(final String id) {
        _value.id = id
    }

    def next(final String next) {
        _value.next = next
    }

    def startLimit(final String startLimit) {
        _value.startLimit = startLimit
    }

    def allowStartIfComplete(final String allowStartIfComplete) {
        _value.allowStartIfComplete = allowStartIfComplete
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

    // Task

    def batchlet(final Closure cl) {
        def that = new GroovyBatchlet()
        _value.task = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def chunk(final Closure cl) {
        def that = new GroovyChunk()
        _value.task = that._value
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

    def partition(final Closure cl) {
        def that = new GroovyPartition()
        _value.partition = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
