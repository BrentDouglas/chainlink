package io.machinecode.nock.spi.factory;

import io.machinecode.nock.spi.element.Property;
import io.machinecode.nock.spi.util.Pair;

import java.util.List;
import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobPropertyContext extends PropertyContext {

    Properties getParameters();

    void addProperty(final Property property);
}
