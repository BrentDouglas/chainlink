package io.machinecode.chainlink.spi.schema;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface DeploymentSchema<Dec extends DeclarationSchema, Prop extends PropertySchema, Job extends JobOperatorSchema<Dec, Prop>>
        extends ScopeSchema<Dec, Prop, Job> {

    String getName();
}
