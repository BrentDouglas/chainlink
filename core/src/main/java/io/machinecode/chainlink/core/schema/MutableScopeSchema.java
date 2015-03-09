package io.machinecode.chainlink.core.schema;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableScopeSchema<Prop extends MutablePropertySchema, Job extends MutableJobOperatorSchema<Prop>>
                extends ScopeSchema<Prop,Job> {

    void setRef(final String ref);

    void setConfigurationLoaders(final List<String> artifactLoaders);

    void setJobOperators(final List<Job> jobOperators);

    void setProperties(final List<Prop> properties);

    Job removeJobOperator(final String name) throws NoJobOperatorWithNameException;

    /**
     * @param jobOperator
     * @throws JobOperatorWithNameExistsException
     * @throws Exception
     */
    void addJobOperator(final JobOperatorSchema<?> jobOperator) throws Exception;
}
