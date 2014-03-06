package io.machinecode.chainlink.jsl.core.inherit.task;

import io.machinecode.chainlink.spi.Mergeable;
import io.machinecode.chainlink.spi.element.task.Task;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableTask<T extends InheritableTask<T>>
        extends Mergeable<T>, Task {
}
