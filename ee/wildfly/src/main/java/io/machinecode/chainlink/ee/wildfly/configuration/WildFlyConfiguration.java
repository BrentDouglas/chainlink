package io.machinecode.chainlink.ee.wildfly.configuration;

import io.machinecode.chainlink.core.configuration.ConfigurationImpl;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class WildFlyConfiguration extends ConfigurationImpl {

    protected WildFlyConfiguration(final _Builder<?> builder) throws Exception {
        super(builder);
    }

    public static class Builder extends _Builder<Builder> {
        @Override
        public ConfigurationImpl build() throws Exception {
            return new WildFlyConfiguration(this);
        }
    }
}