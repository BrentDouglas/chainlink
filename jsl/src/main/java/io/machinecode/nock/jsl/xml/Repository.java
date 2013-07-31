package io.machinecode.nock.jsl.xml;

import io.machinecode.nock.jsl.xml.util.Inheritable;

import java.io.InputStream;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Repository {

    /**
     *
     * @param clazz The type of element to find.
     * @param that The element to find the parent of.
     * @param <T>
     * @return The parent element.
     * @throws ParentNotFoundException If the parent element is not in this repository.
     */
    <T extends Inheritable<T>> T findParent(final Class<T> clazz, final T that) throws ParentNotFoundException;

    /**
     *
     * @param clazz The type of element to find.
     * @param id The id of the element to find.
     * @param id The jsl-name of the element to find.
     * @param <T>
     * @return The parent element.
     * @throws ParentNotFoundException If the parent element is not in this repository.
     */
    <T extends Inheritable<T>> T findParent(final Class<T> clazz, final String id, String jslName) throws ParentNotFoundException;

    /**
     *
     * @param jslName
     * @param stream
     * @return
     */
    XmlJob add(final String jslName, final InputStream stream);
}
