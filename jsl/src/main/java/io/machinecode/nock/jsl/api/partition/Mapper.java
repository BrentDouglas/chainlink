package io.machinecode.nock.jsl.api.partition;

import io.machinecode.nock.jsl.api.PropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Mapper extends Strategy, PropertyReference {

    String ELEMENT = "mapper";
}
