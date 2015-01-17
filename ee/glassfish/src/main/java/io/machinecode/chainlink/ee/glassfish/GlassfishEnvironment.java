package io.machinecode.chainlink.ee.glassfish;

import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.execution.ThreadFactoryLookup;
import io.machinecode.chainlink.core.management.LazyJobOperator;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishConfiguration;
import io.machinecode.chainlink.ee.glassfish.configuration.GlassfishSubSystem;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import org.glassfish.internal.data.ApplicationInfo;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GlassfishEnvironment implements Environment, AutoCloseable {

    private final ConcurrentMap<String, App> operators = new ConcurrentHashMap<>();
    private SubSystemModelImpl model;
    private final ThreadFactoryLookup threadFactory;
    private Lock lock = new ReentrantLock();

    public GlassfishEnvironment(final ThreadFactoryLookup threadFactory) {
        this.threadFactory = threadFactory;
    }

    public void reload(final GlassfishSubSystem subSystem) throws Exception {
        lock.lock();
        try {
            if (this.model == null) {
                throw new IllegalStateException();
            }
            final ClassLoader loader = this.model.getClassLoader();
            final SubSystemModelImpl model = new SubSystemModelImpl(loader);
            GlassfishConfiguration.configureSubSystem(model, subSystem, loader);
            //TODO This now need to reload operators
            this.model = model;
        } finally {
            lock.unlock();
        }
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
        lock.lock();
        try {
            model = this.model = new SubSystemModelImpl(loader);
        } finally {
            lock.unlock();
        }
        GlassfishConfiguration.configureSubSystem(model, subSystem, loader);
    }

    public void addApplication(final ApplicationInfo info) throws Exception {
        final SubSystemModelImpl model;
        lock.lock();
        try {
            if (this.model == null) {
                throw new IllegalStateException(); //TODO Message
            }
            model = this.model;
        } finally {
            lock.unlock();
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
