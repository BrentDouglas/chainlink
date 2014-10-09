package io.machinecode.chainlink.core.transport;

import io.machinecode.chainlink.spi.configuration.TransportConfiguration;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class LocalTransport extends BaseTransport<Void> {

    public LocalTransport(final TransportConfiguration configuration) {
        super(configuration);
    }

    @Override
    public long getTimeout() {
        return 0;
    }

    @Override
    public TimeUnit getTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}
