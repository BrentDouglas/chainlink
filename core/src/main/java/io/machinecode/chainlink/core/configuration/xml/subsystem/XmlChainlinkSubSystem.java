package io.machinecode.chainlink.core.configuration.xml.subsystem;

import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.configuration.def.SubSystemDef;
import io.machinecode.chainlink.core.configuration.xml.XmlDeclaration;
import io.machinecode.chainlink.core.configuration.xml.XmlDeployment;
import io.machinecode.chainlink.core.configuration.xml.XmlJobOperator;
import io.machinecode.chainlink.core.configuration.xml.XmlProperty;
import io.machinecode.chainlink.core.configuration.xml.XmlScope;
import io.machinecode.chainlink.spi.configuration.SubSystemConfiguration;
import io.machinecode.chainlink.spi.exception.ConfigurationException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlRootElement(namespace = XmlChainlinkSubSystem.NAMESPACE, name = XmlChainlinkSubSystem.ELEMENT)
@XmlAccessorType(NONE)
public class XmlChainlinkSubSystem extends XmlScope implements SubSystemDef<XmlDeployment, XmlDeclaration, XmlProperty, XmlJobOperator> {

    public static final String ELEMENT = "subsystem";

    public static final String SCHEMA_URL = "http://machinecode.io/xml/ns/chainlink/subsystem/chainlink-subsystem_1_0.xsd";
    public static final String NAMESPACE = "http://machinecode.io/xml/ns/chainlink/subsystem";

    @XmlElement(name = "deployment", namespace = XmlChainlinkSubSystem.NAMESPACE, required = false)
    private List<XmlDeployment> deployments = new ArrayList<>(0);

    @Override
    public List<XmlDeployment> getDeployments() {
        return deployments;
    }

    @Override
    public void setDeployments(final List<XmlDeployment> deployments) {
        this.deployments = deployments;
    }

    public void configureSubSystem(final SubSystemModelImpl model, final ClassLoader classLoader) throws Exception {
        configureScope(model, classLoader);
        for (final XmlDeployment deployment : this.deployments) {
            deployment.configureDeployment(model.getDeployment(deployment.getName()), classLoader);
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
        read(stream).configureSubSystem(model, loader);
    }

    public static XmlChainlinkSubSystem read(final InputStream stream) throws Exception {
        try {
            final JAXBContext jaxb = JAXBContext.newInstance(XmlChainlinkSubSystem.class);
            final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            return (XmlChainlinkSubSystem) unmarshaller.unmarshal(stream);
        } finally {
            stream.close();
        }
    }

    public void write(final OutputStream stream) throws Exception {
        final JAXBContext jaxb = JAXBContext.newInstance(XmlChainlinkSubSystem.class);
        final Marshaller marshaller = jaxb.createMarshaller();
        final XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(stream);
        marshaller.marshal(this, writer);
    }
}
