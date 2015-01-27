package io.machinecode.chainlink.spi.jsl.transition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface TerminatingTransition extends Transition {

    String getExitStatus();

}
