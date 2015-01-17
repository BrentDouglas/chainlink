package io.machinecode.chainlink.core.configuration.def;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface SubSystemDef<Dep extends DeploymentDef<Dec,Prop,Job>, Dec extends DeclarationDef, Prop extends PropertyDef, Job extends JobOperatorDef<Dec, Prop>>
        extends ScopeDef<Dec, Prop, Job> {

    List<Dep> getDeployments();

    void setDeployments(final List<Dep> deployments);
}
