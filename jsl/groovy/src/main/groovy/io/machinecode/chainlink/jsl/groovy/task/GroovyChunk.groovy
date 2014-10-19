package io.machinecode.chainlink.jsl.groovy.task

import io.machinecode.chainlink.jsl.fluent.task.FluentChunk

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
public class GroovyChunk {

    final FluentChunk _value = new FluentChunk();

    def checkpointPolicy(final String checkpointPolicy) {
        _value.checkpointPolicy = checkpointPolicy
    }

    def itemCount(final String itemCount) {
        _value.itemCount = itemCount
    }

    def timeLimit(final String timeLimit) {
        _value.timeLimit = timeLimit
    }

    def skipLimit(final String skipLimit) {
        _value.skipLimit = skipLimit
    }

    def retryLimit(final String retryLimit) {
        _value.retryLimit = retryLimit
    }

    def reader(final Closure cl) {
        def that = new GroovyItemReader()
        _value.reader = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def processor(final Closure cl) {
        def that = new GroovyItemProcessor()
        _value.processor = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def writer(final Closure cl) {
        def that = new GroovyItemWriter()
        _value.writer = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def checkpointAlgorithm(final Closure cl) {
        def that = new GroovyCheckpointAlgorithm()
        _value.checkpointAlgorithm = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def skippableExceptionClasses(final Closure cl) {
        def that = new GroovyExceptionClassFilter()
        _value.skippableExceptionClasses = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def retryableExceptionClasses(final Closure cl) {
        def that = new GroovyExceptionClassFilter()
        _value.retryableExceptionClasses = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }

    def noRollbackExceptionClasses(final Closure cl) {
        def that = new GroovyExceptionClassFilter()
        _value.noRollbackExceptionClasses = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
