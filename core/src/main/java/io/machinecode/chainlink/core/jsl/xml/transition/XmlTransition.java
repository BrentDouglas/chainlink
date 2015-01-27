package io.machinecode.chainlink.core.jsl.xml.transition;

import io.machinecode.chainlink.spi.jsl.inherit.Copyable;
import io.machinecode.chainlink.spi.jsl.transition.Transition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface XmlTransition<T extends XmlTransition<T>> extends Copyable<T>, Transition {
}
