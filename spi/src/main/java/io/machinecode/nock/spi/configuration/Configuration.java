package io.machinecode.nock.spi.configuration;

import io.machinecode.nock.spi.Repository;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Configuration {

    ClassLoader getClassLoader();

    Repository getRepository();
}
