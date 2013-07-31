package io.machinecode.nock.jsl.xml.type;

import io.machinecode.nock.jsl.api.type.Type;
import io.machinecode.nock.jsl.xml.Repository;
import io.machinecode.nock.jsl.xml.util.Copyable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface XmlType<T extends XmlType<T>> extends Copyable<T>, Type {

    @Override
    String getId();

    T inherit(final Repository repository);
}
