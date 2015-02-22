package io.machinecode.chainlink.rt.tomee;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.Environment;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.schema.Configure;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import org.apache.openejb.AppContext;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.event.AssemblerAfterApplicationCreated;
import org.apache.openejb.assembler.classic.event.AssemblerBeforeApplicationDestroyed;
import org.apache.openejb.assembler.classic.event.ContainerSystemPostCreate;
import org.apache.openejb.assembler.classic.event.ContainerSystemPreDestroy;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.observer.Observes;
import org.apache.openejb.observer.event.ObserverAdded;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TomEEEnvironment implements Environment {

    private final ConcurrentMap<String, App> operators = new ConcurrentHashMap<>();
    private SubSystemModelImpl model;

    public void init(@Observes final ObserverAdded event) {
        if (event.getObserver() == this) {
            Chainlink.setEnvironment(this);
        }
    }

    @Override
    public ExtendedJobOperator getJobOperator(final String name) throws NoConfigurationWithIdException {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                final JobOperatorImpl op = app.ops.get(name);
                if (op == null) {
                    throw new NoConfigurationWithIdException("No configuration for id: " + name); //TODO Message
                }
                return op;
            }
        }
        throw new NoConfigurationWithIdException("Chainlink not configured for TCCL: " + tccl); //TODO Message
    }

    @Override
    public SubSystemSchema<?,?,?,?> getConfiguration() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public SubSystemSchema<?,?,?,?> setConfiguration(final Configure configure) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void reload() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public Map<String, JobOperatorImpl> getJobOperators() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                return Collections.unmodifiableMap(app.ops);
            }
        }
        return Collections.emptyMap();
    }

    private synchronized SubSystemModelImpl ensureModel() {
        if (model != null) {
            return model;
        }
        final SystemInstance system = SystemInstance.get();
        final ClassLoader loader = system.getClassLoader();
        return model = new SubSystemModelImpl(loader);
    }

    public void postCreateSubsystem(@Observes final ContainerSystemPostCreate event) throws Exception {
        final SubSystemModelImpl model = ensureModel();
        final SystemInstance system = SystemInstance.get();
        final ClassLoader loader = system.getClassLoader();
        final String subsystemXml = system.getProperty(Constants.CHAINLINK_SUBSYSTEM_XML, Constants.Defaults.CHAINLINK_SUBSYSTEM_XML);
        final File conf = system.getConf(subsystemXml);
        if (conf != null && conf.isFile()) {
            try (final InputStream stream = new FileInputStream(conf)) {
                XmlChainlinkSubSystem.configureSubSystemFromStream(model, loader, stream);
            }
        }
    }

    public void preDestroySubsystem(@Observes final ContainerSystemPreDestroy event) {
        //TODO
    }

    public void postCreateApp(@Observes final AssemblerAfterApplicationCreated event) throws Exception {
        final SubSystemModelImpl model = ensureModel();
        final SystemInstance system = SystemInstance.get();
        final AppInfo info = event.getApp();
        final AppContext context = event.getContext();
        final ClassLoader loader = context.getClassLoader();
        final DeploymentModelImpl deployment = model.findDeployment(info.appId).copy(loader);
        App app = this.operators.get(info.appId);
        if (app == null) {
            app = new App(loader);
            this.operators.put(info.appId, app);
        }
        final String chainlinkXml = system.getProperty(Constants.CHAINLINK_XML, Constants.Defaults.CHAINLINK_XML);
        final InputStream stream = loader.getResourceAsStream(chainlinkXml);
        if (stream != null) {
            deployment.loadChainlinkXml(stream);
        }
        final TomEEConfigurationDefaults defaults = new TomEEConfigurationDefaults(loader);
        boolean haveDefault = false;
        for (final Map.Entry<String, JobOperatorModelImpl> entry : deployment.getJobOperators().entrySet()) {
            final JobOperatorModelImpl jobOperatorModel = entry.getValue();
            defaults.configureJobOperator(jobOperatorModel);
            if (Constants.DEFAULT.equals(entry.getKey())) {
                haveDefault = true;
            }
            app.ops.put(
                    entry.getKey(),
                    jobOperatorModel.createJobOperator()
            );
        }
        if (!haveDefault) {
            final JobOperatorModelImpl defaultModel = deployment.getJobOperator(Constants.DEFAULT);
            defaults.configureJobOperator(defaultModel);
            app.ops.put(
                    Constants.DEFAULT,
                    defaultModel.createJobOperator()
            );
        }
    }

    public void preDestroyApp(@Observes final AssemblerBeforeApplicationDestroyed event) throws Exception {
        final App app = this.operators.remove(
                event.getApp().appId
        );
        if (app == null) {
            return;
        }
        Exception exception = null;
        for (final JobOperatorImpl op : app.ops.values()) {
            try {
                op.close();
            } catch (Exception e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    private static class App {
        final WeakReference<ClassLoader> loader;
        final ConcurrentMap<String, JobOperatorImpl> ops;

        private App(final ClassLoader loader) {
            this.loader = new WeakReference<>(loader);
            this.ops = new ConcurrentHashMap<>();
        }
    }
}
