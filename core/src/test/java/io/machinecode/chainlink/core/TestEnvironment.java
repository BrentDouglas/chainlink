package io.machinecode.chainlink.core;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.schema.Configure;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TestEnvironment  implements Environment, AutoCloseable {
    final JobOperatorImpl operator;

    public TestEnvironment(final JobOperatorImpl operator) {
        this.operator = operator;
    }

    @Override
    public JobOperatorImpl getJobOperator(final String name) throws NoConfigurationWithIdException {
        if (Constants.DEFAULT.equals(name)) {
            return operator;
        }
        throw new NoConfigurationWithIdException("No operator for name " + name);
    }

    @Override
    public SubSystemSchema<?,?,?> getConfiguration() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public SubSystemSchema<?,?,?> setConfiguration(final Configure configure) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void reload() throws Exception {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void close() throws Exception {
        operator.close();
    }
}
