package io.machinecode.chainlink.jsl.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ForwardingList<T> implements List<T> {

    protected final List<T> delegate;

    public ForwardingList(final List<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return delegate.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return delegate.iterator();
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T1> T1[] toArray(final T1[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(final T t) {
        return delegate.add(t);
    }

    @Override
    public boolean remove(final Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public T get(final int index) {
        return delegate.get(index);
    }

    @Override
    public T set(final int index, final T element) {
        return delegate.set(index, element);
    }

    @Override
    public void add(final int index, final T element) {
        delegate.add(index, element);
    }

    @Override
    public T remove(final int index) {
        return delegate.remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<T> subList(final int fromIndex, final int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }
}
