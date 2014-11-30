package io.machinecode.chainlink.spi.element;

import io.machinecode.chainlink.spi.util.Pair;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Property extends Element, Pair<String,String> {

    String ELEMENT = "property";

    String getName();

    String getValue();
}
