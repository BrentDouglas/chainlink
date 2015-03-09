package io.machinecode.chainlink.core.schema;

import io.machinecode.chainlink.core.util.Mutable;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableSubSystemSchema<Dep extends MutableDeploymentSchema<Prop,Job>, Prop extends MutablePropertySchema, Job extends MutableJobOperatorSchema<Prop>>
        extends SubSystemSchema<Dep,Prop,Job>, MutableScopeSchema<Prop, Job>, Mutable<SubSystemSchema<?,?,?>> {

    void setDeployments(final List<Dep> deployments);

    Dep removeDeployment(final String name) throws NoDeploymentWithNameException;

    /**
     *
     * @param deployment
     * @throws DeploymentWithNameExistsException
     * @throws Exception
     */
    void addDeployment(final DeploymentSchema<?,?> deployment) throws Exception;
}
