package io.machinecode.nock.spi.element.execution;

import io.machinecode.nock.spi.element.PropertyReference;
import io.machinecode.nock.spi.element.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Decision extends Execution, PropertyReference {

    String ELEMENT = "decision";

    List<? extends Transition> getTransitions();
}
