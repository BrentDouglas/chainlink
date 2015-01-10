package io.machinecode.chainlink.core.configuration;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.spi.configuration.SubSystemModel;

import java.lang.ref.WeakReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SubSystemModelImpl extends ScopeModelImpl implements SubSystemModel {

    final DeploymentModelImpl deployment;

    public SubSystemModelImpl(final ClassLoader loader) {
        super(new WeakReference<>(loader), new THashSet<String>());
        this.deployment = new DeploymentModelImpl(this);
    }

    @Override
    public DeploymentModelImpl getDeployment() {
        return deployment;
    }
}
