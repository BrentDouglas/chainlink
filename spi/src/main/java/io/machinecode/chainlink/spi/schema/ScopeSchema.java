package io.machinecode.chainlink.spi.schema;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ScopeSchema<Dec extends DeclarationSchema, Prop extends PropertySchema, Job extends JobOperatorSchema<Dec, Prop>>  {

    String getRef();

    List<Dec> getConfigurationLoaders();

    List<Job> getJobOperators();

    Job getJobOperator(final String name);
}
