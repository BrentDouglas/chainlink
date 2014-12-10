package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.spi.exception.ConfigurationException;
import io.machinecode.chainlink.spi.configuration.DeploymentModel;
import io.machinecode.chainlink.spi.configuration.DeploymentConfiguration;

import javax.xml.bind.annotation.XmlAccessorType;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlDeployment extends XmlScope {

    public void configureDeployment(final DeploymentModel model, final ClassLoader loader) throws Exception {
        configureScope(model, loader);
        if (this.factory != null) {
            final DeploymentConfiguration configuration;
            try {
                final Class<?> clazz = loader.loadClass(this.factory);
                configuration = DeploymentConfiguration.class.cast(clazz.newInstance());
            } catch (final Exception e) {
                throw new ConfigurationException("attribute 'factory' must be the fqcn of a class extending " + DeploymentConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureDeployment(model);
        }
    }
}
