package io.machinecode.chainlink.spi.then;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface OnLink {

    void link(final Chain<?> chain);
}
