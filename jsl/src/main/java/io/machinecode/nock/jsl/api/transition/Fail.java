package io.machinecode.nock.jsl.api.transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Fail extends Transition {

    String ELEMENT = "fail";

    String getExitStatus();
}
