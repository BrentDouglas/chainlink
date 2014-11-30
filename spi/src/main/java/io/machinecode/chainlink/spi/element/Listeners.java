package io.machinecode.chainlink.spi.element;


import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Listeners extends Element {

    String ELEMENT = "listeners";

    List<? extends Listener> getListeners();
}
