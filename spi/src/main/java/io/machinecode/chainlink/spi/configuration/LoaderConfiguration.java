package io.machinecode.chainlink.spi.configuration;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface LoaderConfiguration extends BaseConfiguration {

    ClassLoader getClassLoader();
}