package io.machinecode.chainlink.spi.element;

import io.machinecode.chainlink.spi.util.Pair;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Property extends Element, Pair<String,String> {

    String ELEMENT = "property";

    String getName();

    String getValue();
}
