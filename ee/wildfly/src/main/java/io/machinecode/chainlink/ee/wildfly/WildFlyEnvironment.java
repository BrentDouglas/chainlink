package io.machinecode.chainlink.ee.wildfly;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.util.Messages;

import java.util.Collections;
import java.util.Map;

/**
 * TODO
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class WildFlyEnvironment implements Environment {

    @Override
    public JobOperatorImpl getJobOperator(final String id) throws NoConfigurationWithIdException {
        throw new NoConfigurationWithIdException(Messages.format("CHAINLINK-031004.no.configuration.with.id", id));
    }

    @Override
    public Map<String, JobOperatorImpl> getJobOperators() {
        return Collections.emptyMap();
    }
}
