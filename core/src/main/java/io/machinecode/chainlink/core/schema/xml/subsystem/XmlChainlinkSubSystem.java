package io.machinecode.chainlink.core.schema.xml.subsystem;

import io.machinecode.chainlink.core.util.Copy;
import io.machinecode.chainlink.core.util.Transmute;
import io.machinecode.chainlink.core.schema.xml.CreateXmlJobOperator;
import io.machinecode.chainlink.core.schema.xml.XmlDeployment;
import io.machinecode.chainlink.core.schema.xml.XmlJobOperator;
import io.machinecode.chainlink.core.schema.xml.XmlProperty;
import io.machinecode.chainlink.core.schema.xml.XmlScope;
import io.machinecode.chainlink.core.util.Mutable;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.DeploymentSchema;
import io.machinecode.chainlink.core.schema.DeploymentWithNameExistsException;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.MutableSubSystemSchema;
import io.machinecode.chainlink.core.schema.NoDeploymentWithNameException;
import io.machinecode.chainlink.core.schema.SubSystemSchema;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlRootElement(namespace = XmlChainlinkSubSystem.NAMESPACE, name = XmlChainlinkSubSystem.ELEMENT)
@XmlAccessorType(NONE)
public class XmlChainlinkSubSystem extends XmlScope implements MutableSubSystemSchema<XmlDeployment, XmlProperty, XmlJobOperator>, Mutable<SubSystemSchema<?,?,?>> {

    public static final String ELEMENT = "subsystem";

    public static final String SCHEMA_URL = "http://machinecode.io/xml/ns/chainlink/subsystem/chainlink-subsystem_1_0.xsd";
    public static final String NAMESPACE = "http://machinecode.io/xml/ns/chainlink/subsystem";

    @XmlAttribute(name = "configuration-loaders", required = false)
    private List<String> configurationLoaders = new ArrayList<>(0);

    @XmlElement(name = "job-operator", namespace = XmlChainlinkSubSystem.NAMESPACE, required = false)
    protected List<XmlJobOperator> jobOperators = new ArrayList<>(0);

    @XmlElement(name = "deployment", namespace = XmlChainlinkSubSystem.NAMESPACE, required = false)
    private List<XmlDeployment> deployments = new ArrayList<>(0);

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

    @Override
    public List<XmlDeployment> getDeployments() {
        return deployments;
    }

    @Override
    public void setDeployments(final List<XmlDeployment> deployments) {
        this.deployments = deployments;
    }

    @Override
    public XmlDeployment getDeployment(final String name) {
        for (final XmlDeployment deployment : this.deployments) {
            if (name.equals(deployment.getName())) {
                return deployment;
            }
        }
        return null;
    }

    @Override
    public XmlDeployment removeDeployment(final String name) throws NoDeploymentWithNameException {
        final ListIterator<XmlDeployment> it = this.deployments.listIterator();
        while (it.hasNext()) {
            final XmlDeployment that = it.next();
            if (name.equals(that.getName())) {
                it.remove();
                return that;
            }
        }
        throw new NoDeploymentWithNameException("No deployment with name " + name); //TODO Message
    }

    @Override
    public void addDeployment(final DeploymentSchema<?,?> deployment) throws Exception {
        if (getDeployment(deployment.getName()) != null) {
            throw new DeploymentWithNameExistsException("A deployment already exists with name " + deployment.getName());
        }
        final XmlDeployment op = new XmlDeployment();
        op.accept(deployment);
        this.deployments.add(op);
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

    @Override
    public boolean willAccept(final SubSystemSchema<?,?,?> that) {
        return true;
    }

    @Override
    public void accept(final SubSystemSchema<?,?,?> from, final Op... ops) throws Exception {
        this.setRef(from.getRef());
        Transmute.list(this.configurationLoaders, from.getConfigurationLoaders(), Copy.<String>noop(), ops);
        Transmute.<DeploymentSchema<?,?>, XmlDeployment>list(this.getDeployments(), from.getDeployments(), new CreateXmlDeployment(), ops);
        Transmute.<JobOperatorSchema<?>, XmlJobOperator>list(this.getJobOperators(), from.getJobOperators(), new CreateXmlJobOperator(), ops);
    }
}
