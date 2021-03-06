package io.machinecode.chainlink.jsl.groovy.partition

import io.machinecode.chainlink.core.jsl.fluent.partition.FluentAnalyser
import io.machinecode.chainlink.jsl.groovy.GroovyProperties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GroovyAnalyser {

    final FluentAnalyser _value = new FluentAnalyser();

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
