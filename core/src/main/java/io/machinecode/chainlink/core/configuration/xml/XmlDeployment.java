package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.def.DeploymentDef;
import io.machinecode.chainlink.core.configuration.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.spi.configuration.DeploymentConfiguration;
import io.machinecode.chainlink.spi.exception.ConfigurationException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlRootElement(namespace = XmlChainlinkSubSystem.NAMESPACE, name = "deployment")
@XmlAccessorType(NONE)
public class XmlDeployment extends XmlScope implements DeploymentDef<XmlDeclaration, XmlProperty, XmlJobOperator> {

    @XmlID
    @XmlAttribute(name = "name", required = false)
    protected String name;

    @Override
    public String getName() {
        return name;
    }

    @Override
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

    public static XmlDeployment read(final InputStream stream) throws Exception {
        try {
            final JAXBContext jaxb = JAXBContext.newInstance(XmlChainlink.class);
            final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            final XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(stream);
            return unmarshaller.unmarshal(reader, XmlDeployment.class).getValue();
        } finally {
            stream.close();
        }
    }

    public void write(final OutputStream stream) throws Exception {
        final JAXBContext jaxb = JAXBContext.newInstance(XmlChainlink.class);
        final Marshaller marshaller = jaxb.createMarshaller();
        final XMLStreamWriter writer = XMLOutputFactory.newFactory().createXMLStreamWriter(stream);
        marshaller.marshal(this, writer);
    }
}
