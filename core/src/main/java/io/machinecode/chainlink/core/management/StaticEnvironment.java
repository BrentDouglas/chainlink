package io.machinecode.chainlink.core.management;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StaticEnvironment implements Environment {

    private static final TMap<Configuration, JobOperatorImpl> OPERATORS = new THashMap<Configuration, JobOperatorImpl>();

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
    public ExtendedJobOperator getJobOperator(final Configuration configuration) {
        synchronized (OPERATORS) {
            final JobOperatorImpl cached = OPERATORS.get(configuration);
            if (cached != null) {
                return cached;
            }
            final JobOperatorImpl operator = new JobOperatorImpl(configuration);
            operator.startup();
            OPERATORS.put(configuration, operator);
            return operator;
        }
    }
}
