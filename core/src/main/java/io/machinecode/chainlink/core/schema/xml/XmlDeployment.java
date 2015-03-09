package io.machinecode.chainlink.core.schema.xml;

import io.machinecode.chainlink.core.util.Copy;
import io.machinecode.chainlink.core.util.Transmute;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.core.util.Mutable;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.DeploymentSchema;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.MutableDeploymentSchema;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
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
@XmlRootElement(namespace = XmlChainlinkSubSystem.NAMESPACE, name = "deployment")
@XmlAccessorType(NONE)
public class XmlDeployment extends XmlScope implements MutableDeploymentSchema<XmlProperty, XmlJobOperator>, Mutable<DeploymentSchema<?,?>> {

    @XmlID
    @XmlAttribute(name = "name", required = false)
    protected String name;

    @XmlAttribute(name = "configuration-loaders", required = false)
    private List<String> configurationLoaders = new ArrayList<>(0);

    @XmlElement(name = "job-operator", namespace = XmlChainlink.NAMESPACE, required = false)
    protected List<XmlJobOperator> jobOperators = new ArrayList<>(0);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public List<String> getConfigurationLoaders() {
        return configurationLoaders;
    }

    @Override
    public void setConfigurationLoaders(final List<String> configurationLoaders) {
        this.configurationLoaders = configurationLoaders;
    }

    @Override
    public List<XmlJobOperator> getJobOperators() {
        return jobOperators;
    }

    @Override
    public void setJobOperators(final List<XmlJobOperator> jobOperators) {
        this.jobOperators = jobOperators;
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

    @Override
    public boolean willAccept(final DeploymentSchema<?,?> that) {
        return name == null || name.equals(that.getName());
    }

    @Override
    public void accept(final DeploymentSchema<?,?> from, final Op... ops) throws Exception {
        this.setName(from.getName());
        this.setRef(from.getRef());
        Transmute.list(this.configurationLoaders, from.getConfigurationLoaders(), Copy.<String>noop(), ops);
        Transmute.<JobOperatorSchema<?>, XmlJobOperator>list(this.getJobOperators(), from.getJobOperators(), new CreateXmlJobOperator(), ops);
    }
}
