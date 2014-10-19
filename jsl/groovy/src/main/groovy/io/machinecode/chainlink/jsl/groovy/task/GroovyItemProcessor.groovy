package io.machinecode.chainlink.jsl.groovy.task

import io.machinecode.chainlink.jsl.fluent.task.FluentItemProcessor
import io.machinecode.chainlink.jsl.groovy.GroovyProperties;

/**
 * @author Brent Douglas (brent.n.douglas@gmail.com)
 * @since 1.0
 */
public class GroovyItemProcessor {

    final FluentItemProcessor _value = new FluentItemProcessor();

    def ref(final String ref) {
        _value.ref = ref
    }

    def props(final Closure cl) {
        def that = new GroovyProperties()
        _value.properties = that._value
        def code = cl.rehydrate(that, this, this)
        code.resolveStrategy = Closure.DELEGATE_ONLY
        code()
    }
}
