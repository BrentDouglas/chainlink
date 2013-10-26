package io.machinecode.nock.spi.inject;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface InjectablesProvider {

    void setInjectables(Injectables injectables);

    Injectables getInjectables();
}
