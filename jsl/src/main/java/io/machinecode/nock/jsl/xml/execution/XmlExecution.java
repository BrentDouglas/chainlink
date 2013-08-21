package io.machinecode.nock.jsl.xml.execution;

import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.jsl.xml.Repository;
import io.machinecode.nock.jsl.xml.util.Copyable;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface XmlExecution<T extends XmlExecution<T>> extends Copyable<T>, Execution {

    @Override
    String getId();

    T inherit(final Repository repository);
}
