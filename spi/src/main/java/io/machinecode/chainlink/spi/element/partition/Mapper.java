package io.machinecode.chainlink.spi.element.partition;

import io.machinecode.chainlink.spi.element.PropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Mapper extends Strategy, PropertyReference {

    String ELEMENT = "mapper";
}
