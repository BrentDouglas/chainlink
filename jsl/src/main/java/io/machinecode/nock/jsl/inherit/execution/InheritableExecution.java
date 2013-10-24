package io.machinecode.nock.jsl.inherit.execution;

import io.machinecode.nock.spi.Inheritable;
import io.machinecode.nock.spi.element.execution.Execution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableExecution<T extends InheritableExecution<T>>
        extends Inheritable<T>, Execution {
}
