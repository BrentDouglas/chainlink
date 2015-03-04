package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.SubSystemConfiguration;
import io.machinecode.chainlink.spi.configuration.SubSystemModel;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NoopSubSystemConfiguration implements SubSystemConfiguration {
    @Override
    public void configureSubSystem(final SubSystemModel model) throws Exception {
        //no op
    }
}
