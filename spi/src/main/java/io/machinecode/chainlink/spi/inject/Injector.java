package io.machinecode.chainlink.spi.inject;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Injector {

    /**
     * @param bean The artifact to inject.
     * @return If the artifact was injected by this injector.
     * @throws Exception On any error.
     */
    boolean inject(final Object bean) throws Exception;
}
