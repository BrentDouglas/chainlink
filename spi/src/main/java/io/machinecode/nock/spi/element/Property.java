package io.machinecode.nock.spi.element;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Property extends Element {

    String ELEMENT = "property";

    String getName();

    String getValue();
}
