package io.machinecode.chainlink.ee.glassfish;

import io.machinecode.chainlink.core.configuration.xml.XmlChainlink;
import io.machinecode.chainlink.core.configuration.xml.XmlConfiguration;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.configuration.factory.ConfigurationFactory;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import io.machinecode.chainlink.spi.util.Messages;
import org.glassfish.internal.data.ApplicationInfo;

import javax.naming.InitialContext;
import javax.transaction.TransactionManager;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class GlassfishEnvironment implements Environment {

    private final ConcurrentMap<String, App> operators = new ConcurrentHashMap<>();

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
    public Map<String, JobOperatorImpl> getJobOperators() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                return app.ops;
            }
        }
        return Collections.emptyMap();
    }

    public void addApplication(final ApplicationInfo info) throws Exception {
        App app = this.operators.get(info.getName());
        final ClassLoader loader = info.getAppClassLoader();
        if (app == null) {
            app = new App(loader);
            this.operators.put(info.getName(), app);
        }
        final List<ConfigurationFactory> factories;
        try {
            factories = new ResolvableService<>(Constants.CONFIGURATION_FACTORY_CLASS, ConfigurationFactory.class)
                    .resolve(loader);
        } catch (final Exception e) {
            throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
        try {
            final TransactionManager transactionManager = InitialContext.doLookup("java:appserver/TransactionManager");
            final GlassfishConfigurationDefaults defaults = new GlassfishConfigurationDefaults(loader, transactionManager);

            final Thread thread = Thread.currentThread();
            final ClassLoader tccl = thread.getContextClassLoader();
            thread.setContextClassLoader(loader);
            try {
                boolean haveDefault = false;
                if (factories.isEmpty()) {
                    final InputStream stream = loader.getResourceAsStream("chainlink.xml");
                    if (stream != null) {
                        final JAXBContext jaxb = JAXBContext.newInstance(XmlChainlink.class);
                        final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
                        final XmlChainlink xml = (XmlChainlink) unmarshaller.unmarshal(stream);

                        for (final XmlConfiguration configuration : xml.getConfigurations()) {
                            app.ops.put(
                                    configuration.getName(),
                                    new JobOperatorImpl(GlassfishConfigutation.xmlToBuilder(configuration)
                                            .setConfigurationDefaults(defaults)
                                            .build()
                                    )
                            );
                            if (Constants.DEFAULT_CONFIGURATION.equals(configuration.getName())) {
                                haveDefault = true;
                            }
                        }
                    }
                } else {
                    for (final ConfigurationFactory configuration : factories) {
                        app.ops.put(
                                configuration.getId(),
                                new JobOperatorImpl(configuration.produce()
                                        .setConfigurationDefaults(defaults)
                                        .build()
                                )
                        );
                        if (Constants.DEFAULT_CONFIGURATION.equals(configuration.getId())) {
                            haveDefault = true;
                        }
                    }
                }
                if (!haveDefault) {
                    app.ops.put(
                            Constants.DEFAULT_CONFIGURATION,
                            new JobOperatorImpl(new GlassfishConfigutation.Builder()
                                    .setConfigurationDefaults(defaults)
                                    .build()
                            )
                    );
                }
            } finally {
                thread.setContextClassLoader(tccl);
            }
        } catch (final Exception e) {
            throw new IllegalStateException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
    }

    public void removeApplication(final ApplicationInfo info) throws Exception {
        final App app = this.operators.remove(
                info.getName()
        );
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
