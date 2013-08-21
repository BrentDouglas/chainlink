package io.machinecode.nock.spi.element.transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface TerminatingTransition extends Transition {

    String getExitStatus();

}
