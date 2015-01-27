package io.machinecode.chainlink.spi.jsl.inherit.task;

import io.machinecode.chainlink.spi.jsl.inherit.Mergeable;
import io.machinecode.chainlink.spi.jsl.task.Task;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableTask<T extends InheritableTask<T>>
        extends Mergeable<T>, Task {
}
