package io.machinecode.chainlink.spi.configuration;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface PropertyLookup {

    String getProperty(final String name);

    String getProperty(final String name, final String defaultValue);
}
