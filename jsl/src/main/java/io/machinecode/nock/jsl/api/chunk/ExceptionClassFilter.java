package io.machinecode.nock.jsl.api.chunk;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ExceptionClassFilter {

    Classes getIncludes();

    Classes getExcludes();
}
