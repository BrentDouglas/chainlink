package io.machinecode.chainlink.spi.jsl.execution;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Split extends TransitionExecution {

    String ELEMENT = "split";

    List<? extends Flow> getFlows();
}
