package io.machinecode.chainlink.core.configuration;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@XmlRootElement(namespace = XmlConfiguration.NAMESPACE, name = XmlConfiguration.ELEMENT)
@XmlAccessorType(NONE)
public class XmlConfiguration {

    public static final String ELEMENT = "configuration";

    public static final String SCHEMA_URL = "http://xmlns.io.machinecode/xml/ns/chainlink/chainlink_1_0.xsd";
    public static final String NAMESPACE = "http://xmlns.io.machinecode/xml/ns/chainlink";

    @XmlElement(name = "executor-factory", namespace = NAMESPACE, required = true)
    private XmlFactoryRef executorFactory;

    @XmlElement(name = "execution-repository-factory", namespace = NAMESPACE, required = true)
    private XmlFactoryRef executionRepositoryFactory;

    @XmlElement(name = "class-loader-factory", namespace = NAMESPACE, required = false)
    private XmlFactoryRef classLoaderFactory;

    @XmlElement(name = "transaction-manager-factory", namespace = NAMESPACE, required = false)
    private XmlFactoryRef transactionManagerFactory;

    @XmlElement(name = "job-loader-factory", namespace = NAMESPACE, required = false)
    private List<XmlFactoryRef> jobLoaderFactories = new ArrayList<XmlFactoryRef>(0);

    @XmlElement(name = "artifact-loader-factory", namespace = NAMESPACE, required = false)
    private List<XmlFactoryRef> artifactLoaderFactories = new ArrayList<XmlFactoryRef>(0);

    @XmlElement(name = "injector-factory", namespace = NAMESPACE, required = false)
    private List<XmlFactoryRef> injectorFactories = new ArrayList<XmlFactoryRef>(0);

    @XmlElement(name = "security-check-factory", namespace = NAMESPACE, required = false)
    private List<XmlFactoryRef> securityCheckFactories = new ArrayList<XmlFactoryRef>(0);

    @XmlElement(name = "property", namespace = NAMESPACE, required = false)
    private List<XmlProperty> properties = new ArrayList<XmlProperty>(0);

    public XmlFactoryRef getExecutorFactory() {
        return executorFactory;
    }

    public void setExecutorFactory(final XmlFactoryRef executorFactory) {
        this.executorFactory = executorFactory;
    }

    public XmlFactoryRef getExecutionRepositoryFactory() {
        return executionRepositoryFactory;
    }

    public void setExecutionRepositoryFactory(final XmlFactoryRef executionRepositoryFactory) {
        this.executionRepositoryFactory = executionRepositoryFactory;
    }

    public XmlFactoryRef getClassLoaderFactory() {
        return classLoaderFactory;
    }

    public void setClassLoaderFactory(final XmlFactoryRef classLoaderFactory) {
        this.classLoaderFactory = classLoaderFactory;
    }

    public XmlFactoryRef getTransactionManagerFactory() {
        return transactionManagerFactory;
    }

    public void setTransactionManagerFactory(final XmlFactoryRef transactionManagerFactory) {
        this.transactionManagerFactory = transactionManagerFactory;
    }

    public List<XmlFactoryRef> getJobLoaderFactories() {
        return jobLoaderFactories;
    }

    public void setJobLoaderFactories(final List<XmlFactoryRef> jobLoaderFactories) {
        this.jobLoaderFactories = jobLoaderFactories;
    }

    public List<XmlFactoryRef> getArtifactLoaderFactories() {
        return artifactLoaderFactories;
    }

    public void setArtifactLoaderFactories(final List<XmlFactoryRef> artifactLoaderFactories) {
        this.artifactLoaderFactories = artifactLoaderFactories;
    }

    public List<XmlFactoryRef> getInjectorFactories() {
        return injectorFactories;
    }

    public void setInjectorFactories(final List<XmlFactoryRef> injectorFactories) {
        this.injectorFactories = injectorFactories;
    }

    public List<XmlFactoryRef> getSecurityCheckFactories() {
        return securityCheckFactories;
    }

    public void setSecurityCheckFactories(final List<XmlFactoryRef> securityCheckFactories) {
        this.securityCheckFactories = securityCheckFactories;
    }

    public List<XmlProperty> getProperties() {
        return properties;
    }

    public void setProperties(final List<XmlProperty> properties) {
        this.properties = properties;
    }

    public ConfigurationImpl build() {
        return new ConfigurationImpl(this);
    }
}
