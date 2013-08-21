package io.machinecode.nock.spi.element.task;

import io.machinecode.nock.spi.element.Element;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExceptionClassFilter extends Element {

    List<? extends ExceptionClass> getIncludes();

    List<? extends ExceptionClass> getExcludes();
}
