package io.machinecode.chainlink.inject.cdi;

import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;

import javax.enterprise.inject.spi.BeanManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CdiConfigurationLoader implements ConfigurationLoader {

    private BeanManager beanManager;

    public CdiConfigurationLoader(final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _loader) throws Exception {
        return CdiArtifactLoader._inject(beanManager, as, id, new NamedLiteral(id));
    }
}
