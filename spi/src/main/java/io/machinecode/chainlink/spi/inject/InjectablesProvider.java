package io.machinecode.chainlink.spi.inject;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface InjectablesProvider {

    void setInjectables(Injectables injectables);

    Injectables getInjectables();
}
