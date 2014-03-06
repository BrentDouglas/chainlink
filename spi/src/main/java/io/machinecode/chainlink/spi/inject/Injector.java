package io.machinecode.chainlink.spi.inject;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Injector {

    /**
     * @param bean The artifact to inject.
     * @return If the artifact was injected by this injector.
     * @throws Exception On any error.
     */
    boolean inject(final Object bean) throws Exception;
}
