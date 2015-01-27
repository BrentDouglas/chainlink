package io.machinecode.chainlink.spi.jsl;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Property extends Element {

    String ELEMENT = "property";

    String getName();

    String getValue();
}
