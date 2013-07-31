package io.machinecode.nock.jsl.xml.partition;

import io.machinecode.nock.jsl.api.partition.Mapper;
import io.machinecode.nock.jsl.xml.util.Copyable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface XmlMapper<T extends XmlMapper<T>> extends Copyable<T>, Mapper {

}
