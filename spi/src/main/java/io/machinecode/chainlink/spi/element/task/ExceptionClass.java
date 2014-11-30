package io.machinecode.chainlink.spi.element.task;

import io.machinecode.chainlink.spi.element.Element;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ExceptionClass extends Element {

    String ELEMENT = "class";

    String getClassName();
}
