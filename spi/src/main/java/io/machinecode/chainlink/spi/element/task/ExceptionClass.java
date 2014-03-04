package io.machinecode.chainlink.spi.element.task;

import io.machinecode.chainlink.spi.element.Element;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExceptionClass extends Element {

    String ELEMENT = "class";

    String getClassName();
}
