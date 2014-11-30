package io.machinecode.chainlink.ee.glassfish;

import io.machinecode.chainlink.core.Chainlink;
import org.glassfish.api.StartupRunLevel;
import org.glassfish.api.event.EventListener;
import org.glassfish.api.event.Events;
import org.glassfish.hk2.api.PostConstruct;
import org.glassfish.hk2.runlevel.RunLevel;
import org.glassfish.internal.data.ApplicationInfo;
import org.glassfish.internal.deployment.Deployment;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@Service
@RunLevel(StartupRunLevel.VAL)
public class GlassfishService implements PostConstruct, EventListener {

    @Inject
    private Events events;

    private GlassfishEnvironment environment = new GlassfishEnvironment();

    @Override
    public void postConstruct() {
        events.register(this);
        Chainlink.setEnvironment(this.environment);
    }

    @Override
    public void event(final Event event) {
        try {
            if (event.is(Deployment.APPLICATION_LOADED)) {
                if (event.hook() != null) {
                    environment.addApplication((ApplicationInfo)event.hook());
                }
            } else if (event.is(Deployment.APPLICATION_UNLOADED)) {
                if (event.hook() != null) {
                    environment.removeApplication((ApplicationInfo)event.hook());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
