package io.machinecode.chainlink.spi.expression;

import io.machinecode.chainlink.spi.element.Property;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobPropertyContext extends PropertyContext {

    Properties getParameters();

    void addProperty(final Property property);
}
