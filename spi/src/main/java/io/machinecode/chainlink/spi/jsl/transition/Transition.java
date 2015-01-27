package io.machinecode.chainlink.spi.jsl.transition;

import io.machinecode.chainlink.spi.jsl.Element;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Transition extends Element {

    String getOn();
}
