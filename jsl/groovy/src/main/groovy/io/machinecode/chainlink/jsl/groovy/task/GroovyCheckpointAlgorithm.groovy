package io.machinecode.chainlink.jsl.groovy.task

import io.machinecode.chainlink.jsl.fluent.task.FluentCheckpointAlgorithm
import io.machinecode.chainlink.jsl.groovy.GroovyProperties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GroovyCheckpointAlgorithm {

    final FluentCheckpointAlgorithm _value = new FluentCheckpointAlgorithm();

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
