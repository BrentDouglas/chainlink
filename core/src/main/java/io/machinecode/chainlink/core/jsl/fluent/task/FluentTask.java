package io.machinecode.chainlink.core.jsl.fluent.task;

import io.machinecode.chainlink.spi.Mergeable;
import io.machinecode.chainlink.spi.element.task.Task;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface FluentTask<T extends FluentTask<T>> extends Mergeable<T>, Task {
}
