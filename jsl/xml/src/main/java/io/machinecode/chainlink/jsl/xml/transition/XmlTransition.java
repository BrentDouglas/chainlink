package io.machinecode.chainlink.jsl.xml.transition;

import io.machinecode.chainlink.spi.element.transition.Transition;
import io.machinecode.chainlink.spi.Copyable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface XmlTransition<T extends XmlTransition<T>> extends Copyable<T>, Transition {
}
