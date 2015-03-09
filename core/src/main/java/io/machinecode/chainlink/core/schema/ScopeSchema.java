package io.machinecode.chainlink.core.schema;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ScopeSchema<Prop extends PropertySchema, Job extends JobOperatorSchema<Prop>>  {

    String getRef();

    List<String> getConfigurationLoaders();

    List<Job> getJobOperators();

    List<Prop> getProperties();

    Job getJobOperator(final String name);
}
