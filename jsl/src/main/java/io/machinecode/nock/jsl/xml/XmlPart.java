package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.api.Part;
import io.machinecode.nock.jsl.xml.util.Mergeable;

import javax.xml.bind.annotation.XmlType;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlType
public interface XmlPart<T extends XmlPart<T>> extends Mergeable<T>, Part {
}
