package io.machinecode.nock.spi;

import io.machinecode.nock.spi.element.Element;
import io.machinecode.nock.spi.element.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface PropertiesElement extends Element {

    Properties getProperties();
}
