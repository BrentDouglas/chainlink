package io.machinecode.chainlink.spi.then;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface OnLink {

    void link(final Chain<?> chain);
}
