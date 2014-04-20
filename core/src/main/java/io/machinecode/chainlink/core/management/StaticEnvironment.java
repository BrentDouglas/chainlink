package io.machinecode.chainlink.core.management;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.configuration.ConfigurationManager;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.inject.DependencyInjectionExtension;
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

    private List<DependencyInjectionExtension> extensions;

    @Override
    public void initialize(final List<DependencyInjectionExtension> extensions) {
        this.extensions = extensions;
    }

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
    public ExtendedJobOperator getJobOperator(final String id) {
        synchronized (OPERATORS) {
            final JobOperatorImpl cached = OPERATORS.get(id);
            if (cached != null) {
                return cached;
            }
            final Configuration configuration = ConfigurationManager.loadConfiguration(id);
            final JobOperatorImpl operator = new JobOperatorImpl(configuration);
            operator.startup();
            OPERATORS.put(id, operator);
            for (final DependencyInjectionExtension extension : this.extensions) {
                extension.register(id, new JobOperatorView(operator));
            }
            return operator;
        }
    }
}
