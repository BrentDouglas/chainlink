package io.machinecode.chainlink.jsl.xml.transition;

import io.machinecode.chainlink.spi.element.transition.Transition;
import io.machinecode.chainlink.spi.Copyable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface XmlTransition<T extends XmlTransition<T>> extends Copyable<T>, Transition {
}
