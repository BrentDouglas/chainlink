package io.machinecode.chainlink.core.jsl.xml.partition;

import io.machinecode.chainlink.spi.jsl.inherit.Copyable;
import io.machinecode.chainlink.spi.jsl.partition.Strategy;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface XmlStrategy<T extends XmlStrategy<T>> extends Copyable<T>, Strategy {

}
