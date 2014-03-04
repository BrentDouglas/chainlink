package io.machinecode.chainlink.jsl.fluent.partition;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.element.partition.Strategy;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface FluentStrategy<T extends FluentStrategy<T>> extends Copyable<T>, Strategy {

}
