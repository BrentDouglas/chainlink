package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.spi.element.partition.Strategy;
import io.machinecode.nock.spi.Copyable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface XmlStrategy<T extends XmlStrategy<T>> extends Copyable<T>, Strategy {

}
