package io.machinecode.chainlink.ee.wildfly.service;

import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ConfigurationService implements Service<SubSystemModelImpl> {

    public static final ServiceName SERVICE_NAME = ChainlinkService.SERVICE_NAME.append(WildFlyConstants.MODEL);

    private SubSystemModelImpl model;
    final InjectedValue<ClassLoader> loader;

    public ConfigurationService(final InjectedValue<ClassLoader> loader) {
        this.loader = loader;
    }

    @Override
    public void start(final StartContext context) throws StartException {
        final ClassLoader loader = this.loader.getOptionalValue();
        if (loader == null) {
            throw new StartException();
        }
        this.model = new SubSystemModelImpl(loader);
    }

    @Override
    public void stop(final StopContext context) {
        this.model = null;
    }

    @Override
    public SubSystemModelImpl getValue() throws IllegalStateException, IllegalArgumentException {
        return model;
    }
}
