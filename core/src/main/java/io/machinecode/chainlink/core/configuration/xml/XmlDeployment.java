package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.spi.configuration.DeploymentConfiguration;
import io.machinecode.chainlink.spi.exception.ConfigurationException;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlDeployment extends XmlScope {

    @XmlID
    @XmlAttribute(name = "name", required = false)
    protected String name;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void configureDeployment(final DeploymentModelImpl model, final ClassLoader classLoader) throws Exception {
        configureScope(model, classLoader);
        if (this.ref != null) {
            final DeploymentConfiguration configuration;
            try {
                configuration = model.getConfigurationArtifactLoader().load(this.ref, DeploymentConfiguration.class, classLoader);
            } catch (final Exception e) {
                throw new ConfigurationException("attribute 'ref' must be an injectable " + DeploymentConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureDeployment(model);
        }
    }
}
