package io.machinecode.chainlink.core.configuration.xml.subsystem;

import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.configuration.xml.XmlDeployment;
import io.machinecode.chainlink.core.configuration.xml.XmlScope;
import io.machinecode.chainlink.spi.configuration.SubSystemConfiguration;
import io.machinecode.chainlink.spi.exception.ConfigurationException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.InputStream;

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

    public void configureSubSystem(final SubSystemModelImpl model, final ClassLoader classLoader) throws Exception {
        configureScope(model, classLoader);
        if (this.deployment != null) {
            deployment.configureDeployment(model.getDeployment(), classLoader);
        }
        if (this.ref != null) {
            final SubSystemConfiguration configuration;
            try {
                configuration = model.getConfigurationArtifactLoader().load(this.ref, SubSystemConfiguration.class, classLoader);
            } catch (final Exception e) {
                throw new ConfigurationException("attribute 'ref' must be an injectable " + SubSystemConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureSubSystem(model);
        }
    }

    public static void configureSubSystemFromStream(final SubSystemModelImpl model, final ClassLoader loader, final InputStream stream) throws Exception {
        try {
            final JAXBContext jaxb = JAXBContext.newInstance(XmlChainlinkSubSystem.class);
            final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            final XmlChainlinkSubSystem xml = (XmlChainlinkSubSystem) unmarshaller.unmarshal(stream);

            xml.configureSubSystem(model, loader);
        } finally {
            stream.close();
        }
    }
}
