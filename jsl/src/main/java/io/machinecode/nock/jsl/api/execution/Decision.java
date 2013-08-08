package io.machinecode.nock.jsl.api.execution;

import io.machinecode.nock.jsl.api.PropertyReference;
import io.machinecode.nock.jsl.api.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Decision extends Execution, PropertyReference {

    String ELEMENT = "decision";

    List<? extends Transition> getTransitions();
}
