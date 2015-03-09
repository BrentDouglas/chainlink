package io.machinecode.chainlink.core.schema;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface SubSystemSchema<Dep extends DeploymentSchema<Prop,Job>, Prop extends PropertySchema, Job extends JobOperatorSchema<Prop>>
        extends ScopeSchema<Prop, Job> {

    List<Dep> getDeployments();

    Dep getDeployment(final String name);
}
