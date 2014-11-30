package io.machinecode.chainlink.spi.expression;

import io.machinecode.chainlink.spi.element.Property;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface JobPropertyContext extends PropertyContext {

    Properties getParameters();

    void addProperty(final Property property);
}
