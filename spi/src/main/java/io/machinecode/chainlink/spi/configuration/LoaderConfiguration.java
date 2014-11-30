package io.machinecode.chainlink.spi.configuration;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface LoaderConfiguration extends Configuration {

    ClassLoader getClassLoader();
}
