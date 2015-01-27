package io.machinecode.chainlink.spi.jsl.execution;

import io.machinecode.chainlink.spi.jsl.PropertyReference;
import io.machinecode.chainlink.spi.jsl.transition.Transition;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Decision extends Execution, PropertyReference {

    String ELEMENT = "decision";

    List<? extends Transition> getTransitions();
}
