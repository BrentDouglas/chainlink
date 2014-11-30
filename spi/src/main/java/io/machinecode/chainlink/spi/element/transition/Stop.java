package io.machinecode.chainlink.spi.element.transition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Stop extends TerminatingTransition {

    String ELEMENT = "stop";

    String getRestart();
}
