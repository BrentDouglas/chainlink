package io.machinecode.chainlink.spi;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobRepository {

    /**
     *
     * @param clazz The type of element to find.
     * @param that The element to find the parent of.
     * @param defaultJobXml The default job XML to search for the parent in.
     * @param <T>
     * @return The parent element.
     * @throws ParentNotFoundException If the parent element is not in this repository.
     */
    <T extends InheritableElement<T>> T findParent(final Class<T> clazz, final T that, final String defaultJobXml) throws ParentNotFoundException;

    /**
     *
     * @param clazz The type of element to find.
     * @param id The id of the element to find.
     * @param id The jsl-name of the element to find.
     * @param <T>
     * @return The parent element.
     * @throws ParentNotFoundException If the parent element is not in this repository.
     */
    <T extends InheritableElement<T>> T findParent(final Class<T> clazz, final String id, String jslName) throws ParentNotFoundException;
}
