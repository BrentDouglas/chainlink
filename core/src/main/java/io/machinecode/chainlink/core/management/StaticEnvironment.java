package io.machinecode.chainlink.core.management;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StaticEnvironment implements Environment {

    private static final TMap<String, JobOperatorImpl> OPERATORS = new THashMap<String, JobOperatorImpl>();

    @Override
    public List<ExtendedJobOperator> getJobOperators() {
        synchronized (OPERATORS) {
            final Collection<JobOperatorImpl> values = OPERATORS.values();
            final List<ExtendedJobOperator> jobOperators = new ArrayList<ExtendedJobOperator>(values.size());
            jobOperators.addAll(values);
            return jobOperators;
        }
    }

    @Override
    public ExtendedJobOperator getJobOperator(final String id) throws NoConfigurationWithIdException {
        synchronized (OPERATORS) {
            final JobOperatorImpl cached = OPERATORS.get(id);
            if (cached != null) {
                return cached;
            }
            final Configuration configuration = Chainlink.configuration(id);
            final JobOperatorImpl operator = new JobOperatorImpl(configuration);
            operator.startup();
            OPERATORS.put(id, operator);
            return operator;
        }
    }
}
