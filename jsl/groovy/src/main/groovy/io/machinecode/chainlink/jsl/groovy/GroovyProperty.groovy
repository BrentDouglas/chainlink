package io.machinecode.chainlink.jsl.groovy

import io.machinecode.chainlink.core.jsl.fluent.FluentProperty;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GroovyProperty {

    final FluentProperty _value = new FluentProperty();

    def name(final String name) {
        _value.name = name
    }

    def value(final String value) {
        _value.value = value
    }
}
