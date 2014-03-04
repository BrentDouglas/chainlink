package io.machinecode.chainlink.spi;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InheritableElement<T extends InheritableElement<T>> extends Inheritable<T> {

    Boolean isAbstract();

    String getParent();

    String getJslName();
}
