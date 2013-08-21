package io.machinecode.nock.spi.element;


import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Listeners extends Element {

    String ELEMENT = "listeners";

    List<? extends Listener> getListeners();
}
