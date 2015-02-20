package io.machinecode.chainlink.ee.wildfly.service;

import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;

import javax.enterprise.inject.spi.BeanManager;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class WildFlyConfigurationLoader implements ConfigurationLoader {

    private final BeanManager beanManager;

    WildFlyConfigurationLoader(final BeanManager manager) {
        beanManager = manager;
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
        if (id.equals("io.machinecode.chainlink.inject.cdi.CdiArtifactLoaderFactory")) {
            if (!as.equals(ArtifactLoaderFactory.class)) {
                throw new ArtifactOfWrongTypeException(); //TODO
            }
            if (beanManager == null) {
                throw new IllegalStateException("Requested CDI artifact loading but no BeanManager is available."); //TODO Message
            }
            return as.cast(loader.loadClass("io.machinecode.chainlink.inject.cdi.CdiArtifactLoaderFactory").getConstructor(BeanManager.class).newInstance(beanManager));
        }
        return null;
    }
}
