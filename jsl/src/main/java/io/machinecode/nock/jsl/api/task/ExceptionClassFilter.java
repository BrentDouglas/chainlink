package io.machinecode.nock.jsl.api.task;

import io.machinecode.nock.jsl.api.Element;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExceptionClassFilter extends Element {

    List<? extends ExceptionClass> getIncludes();

    List<? extends ExceptionClass> getExcludes();
}
