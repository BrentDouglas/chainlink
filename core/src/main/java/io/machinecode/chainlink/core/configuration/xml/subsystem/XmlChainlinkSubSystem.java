package io.machinecode.chainlink.core.configuration.xml.subsystem;

import io.machinecode.chainlink.core.configuration.xml.XmlDeployment;
import io.machinecode.chainlink.core.configuration.xml.XmlScope;
import io.machinecode.chainlink.spi.exception.ConfigurationException;
import io.machinecode.chainlink.spi.configuration.SubSystemModel;
import io.machinecode.chainlink.spi.configuration.SubSystemConfiguration;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlRootElement(namespace = XmlChainlinkSubSystem.NAMESPACE, name = XmlChainlinkSubSystem.ELEMENT)
@XmlAccessorType(NONE)
public class XmlChainlinkSubSystem extends XmlScope {

    public static final String ELEMENT = "subsystem";

    public static final String SCHEMA_URL = "http://io.machinecode/xml/ns/chainlink/subsystem/chainlink-subsystem_1_0.xsd";
    public static final String NAMESPACE = "http://io.machinecode/xml/ns/chainlink/subsystem";

    @XmlElement(name = "deployment", namespace = XmlChainlinkSubSystem.NAMESPACE, required = false)
    private XmlDeployment deployment;

    public XmlDeployment getDeployment() {
        return deployment;
    }

    public void setDeployment(final XmlDeployment deployment) {
        this.deployment = deployment;
    }

    public void configureSubSystem(final SubSystemModel model, final ClassLoader loader) throws Exception {
        configureScope(model, loader);
        if (this.deployment != null) {
            deployment.configureDeployment(model.getDeployment(), loader);
        }
        if (this.factory != null) {
            final SubSystemConfiguration configuration;
            try {
                final Class<?> clazz = loader.loadClass(this.factory);
                configuration = SubSystemConfiguration.class.cast(clazz.newInstance());
            } catch (final Exception e) {
                throw new ConfigurationException("attribute 'factory' must be the fqcn of a class extending " + SubSystemConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureSubSystem(model);
        }
    }
}
