package io.machinecode.chainlink.core.schema;

import io.machinecode.chainlink.core.util.Mutable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableDeploymentSchema<Prop extends MutablePropertySchema, Job extends MutableJobOperatorSchema<Prop>>
        extends MutableScopeSchema<Prop, Job>, DeploymentSchema<Prop,Job>, Mutable<DeploymentSchema<?,?>> {

    void setName(final String name);
}
