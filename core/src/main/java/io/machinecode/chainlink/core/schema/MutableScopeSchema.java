package io.machinecode.chainlink.core.schema;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableScopeSchema<Dec extends MutableDeclarationSchema, Prop extends MutablePropertySchema, Job extends MutableJobOperatorSchema<Dec, Prop>>
                extends ScopeSchema<Dec,Prop,Job> {

    void setRef(final String ref);

    void setConfigurationLoaders(final List<Dec> artifactLoaders);

    void setJobOperators(final List<Job> jobOperators);

    Job removeJobOperator(final String name) throws NoJobOperatorWithNameException;

    /**
     * @param jobOperator
     * @throws JobOperatorWithNameExistsException
     * @throws Exception
     */
    void addJobOperator(final JobOperatorSchema<?,?> jobOperator) throws Exception;
}
