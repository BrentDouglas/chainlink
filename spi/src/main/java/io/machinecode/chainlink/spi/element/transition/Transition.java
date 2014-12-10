package io.machinecode.chainlink.spi.element.transition;

import io.machinecode.chainlink.spi.element.Element;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Transition extends Element {

    String getOn();
}
