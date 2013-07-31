package io.machinecode.nock.jsl.api.transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Stop extends Transition {

    String getExitStatus();

    String getRestart();
}
