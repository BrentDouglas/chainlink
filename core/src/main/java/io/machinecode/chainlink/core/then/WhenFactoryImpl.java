package io.machinecode.chainlink.core.then;

import io.machinecode.chainlink.spi.configuration.LoaderConfiguration;
import io.machinecode.chainlink.spi.configuration.factory.WhenFactory;
import io.machinecode.chainlink.spi.then.When;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class WhenFactoryImpl implements WhenFactory {
    @Override
    public When produce(final LoaderConfiguration configuration) throws Exception {
        return new WhenImpl();
    }
}
