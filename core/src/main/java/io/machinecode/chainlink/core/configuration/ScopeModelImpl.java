package io.machinecode.chainlink.core.configuration;

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.configuration.ScopeModel;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ScopeModelImpl implements ScopeModel {

    final WeakReference<ClassLoader> loader;

    final Map<String, JobOperatorModelImpl> jobOperators = new THashMap<>();
    final Map<String, ConfigurationImpl> configurations = new THashMap<>();
    final Set<String> names;
    boolean loadedConfigurations = false;

    public ScopeModelImpl(final WeakReference<ClassLoader> loader, final Set<String> names) {
        this.loader = loader;
        this.names = names;
    }

    @Override
    public JobOperatorModelImpl getJobOperator(final String name) {
        JobOperatorModelImpl scope = jobOperators.get(name);
        if (scope == null) {
            if (names.contains(name)) {
                throw new RuntimeException("operator " + name + " already declared."); //TODO Message, better exception
            }
            jobOperators.put(name, scope = new JobOperatorModelImpl(name, this, loader));
        }
        return scope;
    }

    public Map<String, JobOperatorModelImpl> getJobOperators() {
        return jobOperators;
    }

    public ConfigurationImpl getConfiguration(final String name) throws Exception {
        loadConfigurations();
        return this.configurations.get(name);
    }

    private synchronized void loadConfigurations() throws Exception {
        if (loadedConfigurations) {
            return;
        }
        for (final Map.Entry<String, JobOperatorModelImpl> entry : jobOperators.entrySet()) {
            this.configurations.put(entry.getKey(), new ConfigurationImpl(entry.getValue()));
        }
        this.loadedConfigurations = true;
    }
}
