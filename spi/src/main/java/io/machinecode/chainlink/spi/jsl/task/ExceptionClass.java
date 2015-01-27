package io.machinecode.chainlink.spi.jsl.task;

import io.machinecode.chainlink.spi.jsl.Element;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExceptionClass extends Element {

    String ELEMENT = "class";

    String getClassName();
}
