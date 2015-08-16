/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @authors tag. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.machinecode.chainlink.core.schema.xml;

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
public class XmlJobOperator implements MutableJobOperatorSchema<XmlProperty>, Mutable<JobOperatorSchema<?>> {

    @XmlID
    @XmlAttribute(name = "name", required = true)
    private String name;

    @XmlAttribute(name = "ref", required = false)
    protected String ref;

    @XmlAttribute(name = "class-loader", required = false)
    private String classLoader;

    @XmlAttribute(name = "transaction-manager", required = false)
    private String transactionManager;

    @XmlAttribute(name = "marshalling", required = false)
    private String marshalling;

    @XmlAttribute(name = "mbean-server", required = false)
    private String mBeanServer;

    @XmlAttribute(name = "repository", required = false)
    private String repository;

    @XmlAttribute(name = "registry", required = false)
    private String registry;

    @XmlAttribute(name = "transport", required = false)
    private String transport;

    @XmlAttribute(name = "executor", required = false)
    private String executor;

    @XmlAttribute(name = "job-loaders", required = false)
    private List<String> jobLoaders = new ArrayList<>(0);

    @XmlAttribute(name = "artifact-loaders", required = false)
    private List<String> artifactLoaders = new ArrayList<>(0);

    @XmlAttribute(name = "securities", required = false)
    private List<String> securities = new ArrayList<>(0);

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
    public String getClassLoader() {
        return classLoader;
    }

    @Override
    public void setClassLoader(final String classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public String getTransactionManager() {
        return transactionManager;
    }

    @Override
    public void setTransactionManager(final String transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public String getMarshalling() {
        return marshalling;
    }

    @Override
    public void setMarshalling(final String marshalling) {
        this.marshalling = marshalling;
    }

    @Override
    public String getMBeanServer() {
        return mBeanServer;
    }

    @Override
    public void setMBeanServer(final String mBeanServer) {
        this.mBeanServer = mBeanServer;
    }

    @Override
    public String getRepository() {
        return repository;
    }

    @Override
    public void setRepository(final String repository) {
        this.repository = repository;
    }

    @Override
    public String getRegistry() {
        return registry;
    }

    @Override
    public void setRegistry(final String registry) {
        this.registry = registry;
    }

    @Override
    public String getTransport() {
        return transport;
    }

    @Override
    public void setTransport(final String transport) {
        this.transport = transport;
    }

    @Override
    public String getExecutor() {
        return executor;
    }

    @Override
    public void setExecutor(final String executor) {
        this.executor = executor;
    }

    @Override
    public List<String> getJobLoaders() {
        return jobLoaders;
    }

    @Override
    public void setJobLoaders(final List<String> jobLoaders) {
        this.jobLoaders = jobLoaders;
    }

    @Override
    public List<String> getArtifactLoaders() {
        return artifactLoaders;
    }

    @Override
    public void setArtifactLoaders(final List<String> artifactLoaders) {
        this.artifactLoaders = artifactLoaders;
    }

    @Override
    public List<String> getSecurities() {
        return securities;
    }

    @Override
    public void setSecurities(final List<String> securities) {
        this.securities = securities;
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
    public boolean willAccept(final JobOperatorSchema<?> that) {
        return name == null || name.equals(that.getName());
    }

    @Override
    public void accept(final JobOperatorSchema<?> from, final Op... ops) throws Exception {
        this.setName(from.getName());
        this.setRef(from.getRef());
        this.setArtifactLoaders(from.getArtifactLoaders());
        this.setSecurities(from.getSecurities());
        this.setJobLoaders(from.getJobLoaders());

        this.setClassLoader(from.getClassLoader());
        this.setTransactionManager(from.getTransactionManager());
        this.setMarshalling(from.getMarshalling());
        this.setMBeanServer(from.getMBeanServer());
        this.setRepository(from.getRepository());
        this.setRegistry(from.getRegistry());
        this.setTransport(from.getTransport());
        this.setExecutor(from.getExecutor());

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
