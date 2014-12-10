package io.machinecode.chainlink.jsl.xml.partition;

import io.machinecode.chainlink.spi.element.partition.Strategy;
import io.machinecode.chainlink.spi.Copyable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface XmlStrategy<T extends XmlStrategy<T>> extends Copyable<T>, Strategy {

}
