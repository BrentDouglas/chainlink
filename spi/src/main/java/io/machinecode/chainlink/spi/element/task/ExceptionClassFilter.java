package io.machinecode.chainlink.spi.element.task;

import io.machinecode.chainlink.spi.element.Element;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface ExceptionClassFilter extends Element {

    List<? extends ExceptionClass> getIncludes();

    List<? extends ExceptionClass> getExcludes();
}
