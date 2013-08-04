package io.machinecode.nock.jsl.api.transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Next extends Transition {

    String ELEMENT = "next";

    String getTo();
}
