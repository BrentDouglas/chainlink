package io.machinecode.nock.spi.element;

import io.machinecode.nock.spi.util.Pair;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Property extends Element, Pair<String,String> {

    String ELEMENT = "property";

    String getName();

    String getValue();
}
