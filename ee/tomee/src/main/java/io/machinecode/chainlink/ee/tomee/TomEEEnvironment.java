package io.machinecode.chainlink.ee.tomee;

import io.machinecode.chainlink.core.Chainlink;
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
import org.apache.openejb.AppContext;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.event.AssemblerAfterApplicationCreated;
import org.apache.openejb.assembler.classic.event.AssemblerBeforeApplicationDestroyed;
import org.apache.openejb.observer.Observes;
import org.apache.openejb.observer.event.ObserverAdded;

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
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class TomEEEnvironment implements Environment {

    private final ConcurrentMap<String, App> operators = new ConcurrentHashMap<String, App>();

    public void init(@Observes final ObserverAdded event) {
        if (event.getObserver() == this) {
            Chainlink.setEnvironment(this);
        }
    }

    @Override
    public ExtendedJobOperator getJobOperator(final String id) throws NoConfigurationWithIdException {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                return app.ops.get(id);
            }
        }
        throw new NoConfigurationWithIdException("No configuration for id: " + id); //TODO Message
    }

    @Override
    public Map<String, ? extends ExtendedJobOperator> getJobOperators() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                return app.ops;
            }
        }
        return Collections.emptyMap();
    }

    public void postCreateApp(@Observes final AssemblerAfterApplicationCreated event) {
        final AppInfo info = event.getApp();
        final AppContext context = event.getContext();
        final ClassLoader loader = context.getClassLoader();
        App app = this.operators.get(info.appId);
        if (app == null) {
            app = new App(loader);
            this.operators.put(info.appId, app);
        }
        final List<ConfigurationFactory> factories;
        try {
            factories = new ResolvableService<ConfigurationFactory>(Constants.CONFIGURATION_FACTORY_CLASS, ConfigurationFactory.class)
                    .resolve(loader);
        } catch (final Exception e) {
            throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
        try {
            final TransactionManager transactionManager = context.getSystemInstance().getComponent(TransactionManager.class);
            final TomEEConfigurationDefaults defaults = new TomEEConfigurationDefaults(loader, transactionManager);

            boolean haveDefault = false;
            if (factories.isEmpty()) {
                final InputStream stream = loader.getResourceAsStream("chainlink.xml");
                if (stream != null) {
                    final JAXBContext jaxb = JAXBContext.newInstance(XmlChainlink.class);
                    final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
                    final XmlChainlink xml = (XmlChainlink) unmarshaller.unmarshal(stream);

                    for (final XmlConfiguration configuration : xml.getConfigurations()) {
                        app.ops.put(
                                configuration.getId(),
                                new JobOperatorImpl(TomEEConfigutation.xmlToBuilder(configuration)
                                        .setConfigurationDefaults(defaults)
                                        .build()
                                )
                        );
                        if (Constants.DEFAULT_CONFIGURATION.equals(configuration.getId())) {
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
                        new JobOperatorImpl(new TomEEConfigutation.Builder()
                                .setConfigurationDefaults(defaults)
                                .build()
                        )
                );
            }
        } catch (final Exception e) {
            throw new IllegalStateException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
        }
    }

    public void preDestroyApp(@Observes final AssemblerBeforeApplicationDestroyed event) throws Exception {
        final App app = this.operators.remove(
                event.getApp().appId
        );
        Exception exception = null;
        for (final JobOperatorImpl x : app.ops.values()) {
            try {
                x.close();
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
            this.loader = new WeakReference<ClassLoader>(loader);
            this.ops = new ConcurrentHashMap<String, JobOperatorImpl>();
        }
    }
}
