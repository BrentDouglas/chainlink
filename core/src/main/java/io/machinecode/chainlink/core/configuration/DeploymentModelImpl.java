package io.machinecode.chainlink.core.configuration;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.spi.configuration.DeploymentModel;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DeploymentModelImpl extends ScopeModelImpl implements DeploymentModel {

    public DeploymentModelImpl(final WeakReference<ClassLoader> loader, final Set<String> names) {
        super(loader, names);
    }

    private DeploymentModelImpl(final DeploymentModelImpl that) {
        super(that.loader, new THashSet<>(that.names));
        for (final Map.Entry<String, JobOperatorModelImpl> entry : that.jobOperators.entrySet()) {
            this.jobOperators.put(entry.getKey(), entry.getValue().copy(this));
        }
    }

    public DeploymentModelImpl copy() {
        return new DeploymentModelImpl(this);
    }
}
