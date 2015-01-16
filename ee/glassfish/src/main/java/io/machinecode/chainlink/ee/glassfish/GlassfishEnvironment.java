package io.machinecode.chainlink.ee.glassfish;

import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.configuration.xml.XmlChainlink;
import io.machinecode.chainlink.core.configuration.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.core.execution.ThreadFactoryLookup;
import io.machinecode.chainlink.core.management.LazyJobOperator;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import org.glassfish.internal.data.ApplicationInfo;

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
public class GlassfishEnvironment implements Environment, AutoCloseable {

    private final ConcurrentMap<String, App> operators = new ConcurrentHashMap<>();
    private SubSystemModelImpl model;
    private final ThreadFactoryLookup threadFactory;

    public GlassfishEnvironment(final ThreadFactoryLookup threadFactory) {
        this.threadFactory = threadFactory;
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
                return app.ops;
            }
        }
        return Collections.emptyMap();
    }

    public void addSubsystem(final ClassLoader loader) throws Exception {
        this.model = new SubSystemModelImpl(loader);
        final String subsystemXml = System.getProperty(Constants.CHAINLINK_SUBSYSTEM_XML, Constants.Defaults.CHAINLINK_SUBSYSTEM_XML);
        //TODO This should come out of the config dir
        final InputStream stream = loader.getResourceAsStream(subsystemXml);
        if (stream != null) {
            XmlChainlinkSubSystem.configureSubSystemFromStream(model, loader, stream);
        }
    }

    public void addApplication(final ApplicationInfo info) throws Exception {
        if (model == null) {
            throw new IllegalStateException(); //TODO Message
        }
        App app = this.operators.get(info.getName());
        final ClassLoader loader = info.getAppClassLoader();
        if (app == null) {
            app = new App(loader);
            this.operators.put(info.getName(), app);
        }
        final DeploymentModelImpl deployment = this.model.getDeployment().copy(loader);

        final String chainlinkXml = System.getProperty(Constants.CHAINLINK_XML, Constants.Defaults.CHAINLINK_XML);
        final InputStream stream = loader.getResourceAsStream(chainlinkXml);
        if (stream != null) {
            XmlChainlink.configureDeploymentFromStream(deployment, loader, stream);
        }
        final GlassfishConfigurationDefaults defaults = new GlassfishConfigurationDefaults(loader, threadFactory);
        boolean haveDefault = false;
        for (final Map.Entry<String, JobOperatorModelImpl> entry : deployment.getJobOperators().entrySet()) {
            final JobOperatorModelImpl jobOperatorModel = entry.getValue();
            defaults.configureJobOperator(jobOperatorModel);
            if (Constants.DEFAULT_CONFIGURATION.equals(entry.getKey())) {
                haveDefault = true;
            }
            final LazyJobOperator op = new LazyJobOperator(jobOperatorModel);
            op.open(jobOperatorModel.getConfiguration());
            app.ops.put(
                    entry.getKey(),
                    op
            );
        }
        if (!haveDefault) {
            final JobOperatorModelImpl defaultModel = deployment.getJobOperator(Constants.DEFAULT_CONFIGURATION);
            defaults.configureJobOperator(defaultModel);
            final LazyJobOperator op = new LazyJobOperator(defaultModel);
            op.open(defaultModel.getConfiguration());
            app.ops.put(
                    Constants.DEFAULT_CONFIGURATION,
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
