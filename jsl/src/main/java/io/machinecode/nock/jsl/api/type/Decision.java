package io.machinecode.nock.jsl.api.type;

import io.machinecode.nock.jsl.api.PropertyReference;
import io.machinecode.nock.jsl.api.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Decision extends Type, PropertyReference {

    List<Transition> getTransitions();
}
