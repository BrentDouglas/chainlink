package io.machinecode.chainlink.rt.glassfish;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.core.execution.ThreadFactoryLookup;
import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.rt.glassfish.schema.GlassfishSubSystem;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import org.glassfish.api.StartupRunLevel;
import org.glassfish.api.admin.ServerEnvironment;
import org.glassfish.api.event.EventListener;
import org.glassfish.api.event.EventTypes;
import org.glassfish.api.event.Events;
import org.glassfish.concurrent.config.ManagedThreadFactory.ManagedThreadFactoryConfigActivator;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.runlevel.RunLevel;
import org.glassfish.internal.data.ApplicationInfo;
import org.glassfish.internal.deployment.Deployment;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import java.util.concurrent.ThreadFactory;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Service
@RunLevel(StartupRunLevel.VAL)
public class GlassfishService implements PostConstruct, EventListener {

    @Inject
    private Events events;

    @Inject
    @Named(ServerEnvironment.DEFAULT_INSTANCE_NAME)
    private GlassfishSubSystem subSystem;

    @Inject
    private ServiceLocator serviceLocator;

    private GlassfishEnvironment environment;

    @Override
    public void postConstruct() {
        events.register(this);
        environment = new GlassfishEnvironment(new ThreadFactoryLookup(){
            @Override
            public ThreadFactory lookupThreadFactory(final PropertyLookup properties) throws Exception {
                serviceLocator.getService(ManagedThreadFactoryConfigActivator.class);
                return InitialContext.doLookup(properties.getProperty(Constants.THREAD_FACTORY_JNDI_NAME, "concurrent/__defaultManagedThreadFactory"));
            }
        }, subSystem);
        Chainlink.setEnvironment(this.environment);
    }

    @Override
    public void event(final Event event) {
        try {
            if (event.is(EventTypes.SERVER_READY)) {
                environment.addSubsystem(Tccl.get(), subSystem);
            } else if (event.is(Deployment.APPLICATION_STARTED)) {
                if (event.hook() != null) {
                    environment.addApplication((ApplicationInfo) event.hook());
                }
            } else if (event.is(Deployment.APPLICATION_STOPPED)) {
                if (event.hook() != null) {
                    environment.removeApplication((ApplicationInfo) event.hook());
                }
            } else if (event.is(EventTypes.PREPARE_SHUTDOWN)) {
                environment.close();
            }
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void reload() throws Exception {
        environment.reload();
    }

    public GlassfishSubSystem getSubSystem() {
        return subSystem;
    }
}
