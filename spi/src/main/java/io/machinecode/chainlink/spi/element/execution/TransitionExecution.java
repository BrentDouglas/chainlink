package io.machinecode.chainlink.spi.element.execution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TransitionExecution extends Execution {

    String getNext();
}
