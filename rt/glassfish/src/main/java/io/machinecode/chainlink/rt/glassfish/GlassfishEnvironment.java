package io.machinecode.chainlink.rt.glassfish;

import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.execution.ThreadFactoryLookup;
import io.machinecode.chainlink.core.management.LazyJobOperator;
import io.machinecode.chainlink.rt.glassfish.configuration.GlassfishConfiguration;
import io.machinecode.chainlink.rt.glassfish.configuration.GlassfishSubSystem;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Configure;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.schema.SubSystemSchema;
import org.glassfish.internal.data.ApplicationInfo;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GlassfishEnvironment implements Environment, AutoCloseable {

    private final ConcurrentMap<String, App> operators = new ConcurrentHashMap<>();
    private SubSystemModelImpl model;
    private final ThreadFactoryLookup threadFactory;
    private final Object lock = new Object();
    private final GlassfishSubSystem subSystem;

    public GlassfishEnvironment(final ThreadFactoryLookup threadFactory, final GlassfishSubSystem subSystem) {
        this.threadFactory = threadFactory;
        this.subSystem = subSystem;
    }

    @Override
    public ExtendedJobOperator getSubsystemJobOperator(final String name) throws NoConfigurationWithIdException {
        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public ExtendedJobOperator getJobOperator(final String name) throws NoConfigurationWithIdException {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                final LazyJobOperator op = app.ops.get(name);
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
        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public SubSystemSchema<?,?,?,?> setConfiguration(final Configure configure) {
        throw new IllegalStateException("Not implemented yet");
    }

    @Override
    public void reload() throws Exception {
        synchronized (lock) {
            if (this.model == null) {
                throw new IllegalStateException();
            }
            final ClassLoader loader = this.model.getClassLoader();
            final SubSystemModelImpl model = new SubSystemModelImpl(loader);
            GlassfishConfiguration.configureSubSystem(model, subSystem, loader);
            //TODO This now need to reload operators
            this.model = model;
        }
    }

    public Map<String, LazyJobOperator> getJobOperators() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                return Collections.unmodifiableMap(app.ops);
            }
        }
        return Collections.emptyMap();
    }

    public void addSubsystem(final ClassLoader loader, final GlassfishSubSystem subSystem) throws Exception {
        final SubSystemModelImpl model;
        synchronized (lock) {
            model = this.model = new SubSystemModelImpl(loader);
        }
        GlassfishConfiguration.configureSubSystem(model, subSystem, loader);
    }

    public void addApplication(final ApplicationInfo info) throws Exception {
        final SubSystemModelImpl model;
        synchronized (lock) {
            if (this.model == null) {
                throw new IllegalStateException(); //TODO Message
            }
            model = this.model;
        }
        App app = this.operators.get(info.getName());
        final ClassLoader loader = info.getAppClassLoader();
        if (app == null) {
            app = new App(loader);
            this.operators.put(info.getName(), app);
        }
        final DeploymentModelImpl deployment = model.findDeployment(info.getName()).copy(loader);

        deployment.loadChainlinkXml();

        final GlassfishConfigurationDefaults defaults = new GlassfishConfigurationDefaults(loader, threadFactory);
        boolean haveDefault = false;
        for (final Map.Entry<String, JobOperatorModelImpl> entry : deployment.getJobOperators().entrySet()) {
            final JobOperatorModelImpl jobOperatorModel = entry.getValue();
            defaults.configureJobOperator(jobOperatorModel);
            if (Constants.DEFAULT.equals(entry.getKey())) {
                haveDefault = true;
            }
            final LazyJobOperator op = jobOperatorModel.createLazyJobOperator();
            app.ops.put(
                    entry.getKey(),
                    op
            );
        }
        if (!haveDefault) {
            final JobOperatorModelImpl defaultModel = deployment.getJobOperator(Constants.DEFAULT);
            defaults.configureJobOperator(defaultModel);
            final LazyJobOperator op = defaultModel.createLazyJobOperator();
            app.ops.put(
                    Constants.DEFAULT,
                    op
            );
        }
    }

    public void removeApplication(final ApplicationInfo info) throws Exception {
        final App app = this.operators.remove(
                info.getName()
        );
        Exception exception = null;
        for (final LazyJobOperator op : app.ops.values()) {
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

    @Override
    public void close() throws Exception {
        // TODO
    }

    private static class App {
        final WeakReference<ClassLoader> loader;
        final ConcurrentMap<String, LazyJobOperator> ops;

        private App(final ClassLoader loader) {
            this.loader = new WeakReference<>(loader);
            this.ops = new ConcurrentHashMap<>();
        }
    }
}
