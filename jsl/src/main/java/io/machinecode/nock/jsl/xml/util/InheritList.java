package io.machinecode.nock.jsl.xml.util;

import io.machinecode.nock.jsl.util.ForwardingList;
import io.machinecode.nock.jsl.xml.loader.Repository;
import io.machinecode.nock.jsl.xml.execution.XmlExecution;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InheritList<T extends XmlExecution<T> & Copyable<T>> extends ForwardingList<T> implements List<T> {

    public InheritList(final Repository repository, final String defaultJobXml, final List<T> delegate) {
        super(new ArrayList<T>(delegate.size()));
        for (final T that : delegate) {
            if (that instanceof Inheritable) {
                final Inheritable inheritable = (Inheritable)that;
                if (inheritable.isAbstract() != null && inheritable.isAbstract()) {
                    continue;
                }
            }
            this.delegate.add(that.inherit(repository, defaultJobXml));
        }
    }

    public InheritList(final Repository repository, final String defaultJobXml, final List<T>... delegates) {
        super(new ArrayList<T>());
        for (final List<T> delegate : delegates) {
            if (delegate == null) {
                continue;
            }
            for (final T that : delegate) {
                if (that instanceof Inheritable) {
                    final Inheritable inheritable = (Inheritable)that;
                    if (inheritable.isAbstract() != null && inheritable.isAbstract()) {
                        continue;
                    }
                }
                this.delegate.add(that.inherit(repository, defaultJobXml));
            }
        }
    }
}
