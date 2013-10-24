package io.machinecode.nock.jsl.inherit;

import io.machinecode.nock.jsl.util.ForwardingList;
import io.machinecode.nock.spi.Copyable;
import io.machinecode.nock.spi.Inheritable;
import io.machinecode.nock.spi.InheritableElement;
import io.machinecode.nock.spi.JobRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InheritList<T extends Inheritable<T> & Copyable<T>> extends ForwardingList<T> implements List<T> {

    public InheritList(final JobRepository repository, final String defaultJobXml, final List<? extends T> delegate) {
        super(new ArrayList<T>(delegate.size()));
        for (final T that : delegate) {
            if (that instanceof InheritableElement) {
                final InheritableElement inheritable = (InheritableElement)that;
                if (inheritable.isAbstract() != null && inheritable.isAbstract()) {
                    continue;
                }
            }
            this.delegate.add(that.inherit(repository, defaultJobXml));
        }
    }

    public InheritList(final JobRepository repository, final String defaultJobXml, final List<? extends T>... delegates) {
        super(new ArrayList<T>());
        for (final List<? extends T> delegate : delegates) {
            if (delegate == null) {
                continue;
            }
            for (final T that : delegate) {
                if (that instanceof InheritableElement) {
                    final InheritableElement inheritable = (InheritableElement)that;
                    if (inheritable.isAbstract() != null && inheritable.isAbstract()) {
                        continue;
                    }
                }
                this.delegate.add(that.inherit(repository, defaultJobXml));
            }
        }
    }
}
