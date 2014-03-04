package io.machinecode.chainlink.jsl.inherit.execution;

import io.machinecode.chainlink.spi.Inheritable;
import io.machinecode.chainlink.spi.element.execution.Execution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableExecution<T extends InheritableExecution<T>>
        extends Inheritable<T>, Execution {
}
