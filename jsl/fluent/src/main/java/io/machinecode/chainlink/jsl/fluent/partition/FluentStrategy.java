package io.machinecode.chainlink.jsl.fluent.partition;

import io.machinecode.chainlink.spi.Copyable;
import io.machinecode.chainlink.spi.element.partition.Strategy;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface FluentStrategy<T extends FluentStrategy<T>> extends Copyable<T>, Strategy {

}
