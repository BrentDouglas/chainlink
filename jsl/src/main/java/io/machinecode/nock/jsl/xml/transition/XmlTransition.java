package io.machinecode.nock.jsl.xml.transition;

import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.xml.util.Copyable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface XmlTransition<T extends XmlTransition<T>> extends Copyable<T>, Transition {
}
