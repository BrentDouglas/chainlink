package io.machinecode.chainlink.spi.jsl.task;

import io.machinecode.chainlink.spi.jsl.Element;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ExceptionClassFilter extends Element {

    List<? extends ExceptionClass> getIncludes();

    List<? extends ExceptionClass> getExcludes();
}
