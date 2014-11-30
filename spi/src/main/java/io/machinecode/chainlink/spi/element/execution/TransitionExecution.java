package io.machinecode.chainlink.spi.element.execution;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface TransitionExecution extends Execution {

    String getNext();
}
