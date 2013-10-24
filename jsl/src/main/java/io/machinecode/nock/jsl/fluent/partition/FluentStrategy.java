package io.machinecode.nock.jsl.fluent.partition;

import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.element.partition.Strategy;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface FluentStrategy<T extends FluentStrategy<T>> extends Copyable<T>, Strategy {

}
