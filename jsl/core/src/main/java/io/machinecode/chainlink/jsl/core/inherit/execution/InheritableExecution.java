package io.machinecode.chainlink.jsl.core.inherit.execution;

import io.machinecode.chainlink.spi.Inheritable;
import io.machinecode.chainlink.spi.element.execution.Execution;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableExecution<T extends InheritableExecution<T>>
        extends Inheritable<T>, Execution {
}
