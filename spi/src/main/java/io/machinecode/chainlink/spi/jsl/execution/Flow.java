package io.machinecode.chainlink.spi.jsl.execution;

import io.machinecode.chainlink.spi.jsl.transition.Transition;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Flow extends TransitionExecution {

    String ELEMENT = "flow";

    List<? extends Execution> getExecutions();

    List<? extends Transition> getTransitions();
}
