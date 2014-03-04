package io.machinecode.chainlink.jsl.fluent.task;

import io.machinecode.chainlink.spi.Mergeable;
import io.machinecode.chainlink.spi.element.task.Task;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface FluentTask<T extends FluentTask<T>> extends Mergeable<T>, Task {
}
