package io.machinecode.nock.jsl.fluent.task;

import io.machinecode.nock.spi.Mergeable;
import io.machinecode.nock.spi.element.task.Task;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface FluentTask<T extends FluentTask<T>> extends Mergeable<T>, Task {
}
