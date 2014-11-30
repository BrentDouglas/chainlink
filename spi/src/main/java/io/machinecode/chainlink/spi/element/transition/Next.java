package io.machinecode.chainlink.spi.element.transition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Next extends Transition {

    String ELEMENT = "next";

    String getTo();
}
