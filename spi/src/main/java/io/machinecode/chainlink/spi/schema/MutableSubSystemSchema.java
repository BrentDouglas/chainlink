package io.machinecode.chainlink.spi.schema;

import io.machinecode.chainlink.spi.management.Mutable;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface MutableSubSystemSchema<Dep extends MutableDeploymentSchema<Dec,Prop,Job>, Dec extends MutableDeclarationSchema, Prop extends MutablePropertySchema, Job extends MutableJobOperatorSchema<Dec, Prop>>
        extends SubSystemSchema<Dep,Dec,Prop,Job>, MutableScopeSchema<Dec, Prop, Job>, Mutable<SubSystemSchema<?,?,?,?>> {

    List<Dep> getDeployments();

    void setDeployments(final List<Dep> deployments);

    Dep getDeployment(final String name);

    Dep removeDeployment(final String name) throws NoDeploymentWithNameException;

    /**
     *
     * @param deployment
     * @throws DeploymentWithNameExistsException
     * @throws Exception
     */
    void addDeployment(final DeploymentSchema<?,?,?> deployment) throws Exception;
}
