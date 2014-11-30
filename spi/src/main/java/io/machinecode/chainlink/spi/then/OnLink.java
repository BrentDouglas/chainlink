package io.machinecode.chainlink.spi.then;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface OnLink {

    void link(final Chain<?> chain);
}
