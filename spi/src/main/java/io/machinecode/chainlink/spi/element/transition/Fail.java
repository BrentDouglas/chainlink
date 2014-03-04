package io.machinecode.chainlink.spi.element.transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Fail extends TerminatingTransition {

    String ELEMENT = "fail";
}
