package io.machinecode.nock.jsl.inherit.task;

import io.machinecode.nock.spi.Mergeable;
import io.machinecode.nock.spi.element.task.Task;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableTask<T extends InheritableTask<T>>
        extends Mergeable<T>, Task {
}
