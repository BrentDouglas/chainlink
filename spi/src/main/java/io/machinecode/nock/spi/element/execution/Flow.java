package io.machinecode.nock.spi.element.execution;

import io.machinecode.nock.spi.element.transition.Transition;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Flow extends TransitionExecution {

    String ELEMENT = "flow";

    List<? extends Execution> getExecutions();

    List<? extends Transition> getTransitions();
}
