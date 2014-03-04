package io.machinecode.chainlink.spi.element.execution;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Split extends TransitionExecution {

    String ELEMENT = "split";

    List<? extends Flow> getFlows();
}
