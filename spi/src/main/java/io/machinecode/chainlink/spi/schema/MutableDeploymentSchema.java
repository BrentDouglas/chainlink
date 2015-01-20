package io.machinecode.chainlink.spi.schema;

import io.machinecode.chainlink.spi.management.Mutable;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableDeploymentSchema<Dec extends MutableDeclarationSchema, Prop extends MutablePropertySchema, Job extends MutableJobOperatorSchema<Dec, Prop>>
        extends MutableScopeSchema<Dec, Prop, Job>, DeploymentSchema<Dec,Prop,Job>, Mutable<DeploymentSchema<?,?,?>> {

    void setName(final String name);
}
