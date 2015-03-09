package io.machinecode.chainlink.core.schema;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface DeploymentSchema<Prop extends PropertySchema, Job extends JobOperatorSchema<Prop>>
        extends ScopeSchema<Prop, Job> {

    String getName();
}
