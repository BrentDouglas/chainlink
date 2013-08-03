package io.machinecode.nock.jsl.api.task;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExceptionClassFilter {

    List<? extends ExceptionClass> getIncludes();

    List<? extends ExceptionClass> getExcludes();
}
