package io.machinecode.chainlink.spi.schema;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface SubSystemSchema<Dep extends DeploymentSchema<Dec,Prop,Job>, Dec extends DeclarationSchema, Prop extends PropertySchema, Job extends JobOperatorSchema<Dec, Prop>>
        extends ScopeSchema<Dec, Prop, Job> {

    List<Dep> getDeployments();

    void setDeployments(final List<Dep> deployments);

    Dep getDeployment(final String name);

    Dep removeDeployment(final String name) throws NoDeploymentWithNameException;
}
