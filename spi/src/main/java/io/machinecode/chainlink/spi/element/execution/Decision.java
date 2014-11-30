package io.machinecode.chainlink.spi.element.execution;

import io.machinecode.chainlink.spi.element.PropertyReference;
import io.machinecode.chainlink.spi.element.transition.Transition;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Decision extends Execution, PropertyReference {

    String ELEMENT = "decision";

    List<? extends Transition> getTransitions();
}
