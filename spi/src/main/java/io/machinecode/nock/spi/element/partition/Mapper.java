package io.machinecode.nock.spi.element.partition;

import io.machinecode.nock.spi.element.PropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Mapper extends Strategy, PropertyReference {

    String ELEMENT = "mapper";
}
