package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NoopJobOperatorConfiguration implements JobOperatorConfiguration {
    @Override
    public void configureJobOperator(final JobOperatorModel model) throws Exception {
        //no op
    }
}
