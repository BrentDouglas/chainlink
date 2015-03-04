package io.machinecode.chainlink.core.schema.xml;

import io.machinecode.chainlink.core.util.Creator;
import io.machinecode.chainlink.core.util.Transmute;
import io.machinecode.chainlink.core.util.Mutable;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.MutableJobOperatorSchema;

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
@XmlRootElement(namespace = XmlChainlink.NAMESPACE, name = "job-operator")
@XmlAccessorType(NONE)
public class XmlJobOperator implements MutableJobOperatorSchema<XmlDeclaration, XmlProperty>, Mutable<JobOperatorSchema<?,?>> {

    @XmlID
    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "ref", required = false)
    protected String ref;

    @XmlElement(name = "class-loader", namespace = XmlChainlink.NAMESPACE, required = false)
    private XmlDeclaration classLoader;

    @XmlElement(name = "transaction-manager", namespace = XmlChainlink.NAMESPACE, required = false)
    private XmlDeclaration transactionManager;

    @XmlElement(name = "marshalling", namespace = XmlChainlink.NAMESPACE, required = true)
    private XmlDeclaration marshalling;

    @XmlElement(name = "mbean-server", namespace = XmlChainlink.NAMESPACE, required = true)
    private XmlDeclaration mBeanServer;

    @XmlElement(name = "job-loader", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlDeclaration> jobLoaders = new ArrayList<>(0);

    @XmlElement(name = "artifact-loader", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlDeclaration> artifactLoaders = new ArrayList<>(0);

    @XmlElement(name = "security", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlDeclaration> securities = new ArrayList<>(0);

    @XmlElement(name = "repository", namespace = XmlChainlink.NAMESPACE, required = true)
    private XmlDeclaration repository;

    @XmlElement(name = "registry", namespace = XmlChainlink.NAMESPACE, required = true)
    private XmlDeclaration registry;

    @XmlElement(name = "transport", namespace = XmlChainlink.NAMESPACE, required = true)
    private XmlDeclaration transport;

    @XmlElement(name = "executor", namespace = XmlChainlink.NAMESPACE)
    private XmlDeclaration executor;

    @XmlElement(name = "property", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlProperty> properties = new ArrayList<>(0);

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getRef() {
        return ref;
    }

    @Override
    public void setRef(final String ref) {
        this.ref = ref;
    }

    @Override
    public XmlDeclaration getClassLoader() {
        return classLoader;
    }

    @Override
    public void setClassLoader(final XmlDeclaration classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public XmlDeclaration getTransactionManager() {
        return transactionManager;
    }

    @Override
    public void setTransactionManager(final XmlDeclaration transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public XmlDeclaration getMarshalling() {
        return marshalling;
    }

    @Override
    public void setMarshalling(final XmlDeclaration marshalling) {
        this.marshalling = marshalling;
    }

    @Override
    public XmlDeclaration getMBeanServer() {
        return mBeanServer;
    }

    @Override
    public void setMBeanServer(final XmlDeclaration mBeanServer) {
        this.mBeanServer = mBeanServer;
    }

    @Override
    public List<XmlDeclaration> getJobLoaders() {
        return jobLoaders;
    }

    @Override
    public void setJobLoaders(final List<XmlDeclaration> jobLoaders) {
        this.jobLoaders = jobLoaders;
    }

    @Override
    public List<XmlDeclaration> getArtifactLoaders() {
        return artifactLoaders;
    }

    @Override
    public void setArtifactLoaders(final List<XmlDeclaration> artifactLoaders) {
        this.artifactLoaders = artifactLoaders;
    }

    @Override
    public List<XmlDeclaration> getSecurities() {
        return securities;
    }

    @Override
    public void setSecurities(final List<XmlDeclaration> securities) {
        this.securities = securities;
    }

    @Override
    public XmlDeclaration getRepository() {
        return repository;
    }

    @Override
    public void setRepository(final XmlDeclaration repository) {
        this.repository = repository;
    }

    @Override
    public XmlDeclaration getRegistry() {
        return registry;
    }

    @Override
    public void setRegistry(final XmlDeclaration registry) {
        this.registry = registry;
    }

    @Override
    public XmlDeclaration getTransport() {
        return transport;
    }

    @Override
    public void setTransport(final XmlDeclaration transport) {
        this.transport = transport;
    }

    @Override
    public XmlDeclaration getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(final XmlDeclaration executor) {
        this.executor = executor;
    }

    @Override
    public List<XmlProperty> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(final List<XmlProperty> properties) {
        this.properties = properties;
    }

    @Override
    public void setProperty(final String name, final String value) {
        for (final XmlProperty property : this.properties) {
            if (property.getName().equals(name)) {
                property.setValue(value);
                return;
            }
        }
        final XmlProperty property = new XmlProperty();
        property.setName(name);
        property.setValue(value);
        this.properties.add(property);
    }

    @Override
    public boolean willAccept(final JobOperatorSchema<?,?> that) {
        return name == null || name.equals(that.getName());
    }

    @Override
    public void accept(final JobOperatorSchema<?,?> from, final Op... ops) throws Exception {
        this.setName(from.getName());
        this.setRef(from.getRef());
        final Creator<XmlDeclaration> creator = new CreateXmlDeclaration();
        Transmute.list(this.getArtifactLoaders(), from.getArtifactLoaders(), creator, ops);
        Transmute.list(this.getSecurities(), from.getSecurities(), creator, ops);
        Transmute.list(this.getJobLoaders(), from.getJobLoaders(), creator, ops);

        this.setClassLoader(Transmute.item(this.getClassLoader(), from.getClassLoader(), creator, ops));
        this.setTransactionManager(Transmute.item(this.getTransactionManager(), from.getTransactionManager(), creator, ops));
        this.setMarshalling(Transmute.item(this.getMarshalling(), from.getMarshalling(), creator, ops));
        this.setMBeanServer(Transmute.item(this.getMBeanServer(), from.getMBeanServer(), creator, ops));
        this.setRepository(Transmute.item(this.getRepository(), from.getRepository(), creator, ops));
        this.setRegistry(Transmute.item(this.getRegistry(), from.getRegistry(), creator, ops));
        this.setTransport(Transmute.item(this.getTransport(), from.getTransport(), creator, ops));
        this.setExecutor(Transmute.item(this.getExecutor(), from.getExecutor(), creator, ops));

        Transmute.list(this.getProperties(), from.getProperties(), new CreateXmlProperty(), ops);
    }

    public static XmlJobOperator read(final InputStream stream) throws Exception {
        try {
            final JAXBContext jaxb = JAXBContext.newInstance(XmlChainlink.class);
            final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            final XMLStreamReader reader = XMLInputFactory.newFactory().createXMLStreamReader(stream);
            return unmarshaller.unmarshal(reader, XmlJobOperator.class).getValue();
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
