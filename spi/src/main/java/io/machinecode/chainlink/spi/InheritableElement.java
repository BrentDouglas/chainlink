package io.machinecode.chainlink.spi;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface InheritableElement<T extends InheritableElement<T>> extends Inheritable<T> {

    Boolean isAbstract();

    String getParent();

    String getJslName();
}
