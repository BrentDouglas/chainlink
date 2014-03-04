package io.machinecode.chainlink.spi.element.transition;

import io.machinecode.chainlink.spi.element.Element;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Transition extends Element {

    String getOn();
}
