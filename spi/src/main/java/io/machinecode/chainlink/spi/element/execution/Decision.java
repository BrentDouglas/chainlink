package io.machinecode.chainlink.spi.element.execution;

import io.machinecode.chainlink.spi.element.PropertyReference;
import io.machinecode.chainlink.spi.element.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Decision extends Execution, PropertyReference {

    String ELEMENT = "decision";

    List<? extends Transition> getTransitions();
}
