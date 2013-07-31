package io.machinecode.nock.jsl.api.type;

import io.machinecode.nock.jsl.api.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Flow extends Type {

    String getNext();

    List<Type> getTypes();

    List<Transition> getTransitions();
}
