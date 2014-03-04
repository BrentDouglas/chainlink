package io.machinecode.chainlink.spi.factory;

import io.machinecode.chainlink.spi.element.Property;
import io.machinecode.chainlink.spi.util.Pair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobPropertyContext extends PropertyContext {

    Properties getParameters();

    void addProperty(final Property property);
}
