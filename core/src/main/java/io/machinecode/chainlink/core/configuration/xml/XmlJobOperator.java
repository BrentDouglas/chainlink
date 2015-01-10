package io.machinecode.chainlink.core.configuration.xml;

import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.ScopeModelImpl;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.exception.ConfigurationException;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static javax.xml.bind.annotation.XmlAccessType.NONE;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
@XmlAccessorType(NONE)
public class XmlJobOperator {

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
    private List<XmlDeclaration> jobLoader = new ArrayList<>(0);

    @XmlElement(name = "artifact-loader", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlDeclaration> artifactLoader = new ArrayList<>(0);

    @XmlElement(name = "injector", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlDeclaration> injector = new ArrayList<>(0);

    @XmlElement(name = "security", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlDeclaration> security = new ArrayList<>(0);

    @XmlElement(name = "execution-repository", namespace = XmlChainlink.NAMESPACE, required = true)
    private XmlDeclaration executionRepository;

    @XmlElement(name = "registry", namespace = XmlChainlink.NAMESPACE, required = true)
    private XmlDeclaration registry;

    @XmlElement(name = "transport", namespace = XmlChainlink.NAMESPACE, required = true)
    private XmlDeclaration transport;

    @XmlElement(name = "executor", namespace = XmlChainlink.NAMESPACE)
    private XmlDeclaration executor;

    @XmlElement(name = "property", namespace = XmlChainlink.NAMESPACE, required = false)
    private List<XmlProperty> properties = new ArrayList<>(0);

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(final String ref) {
        this.ref = ref;
    }

    public XmlDeclaration getClassLoader() {
        return classLoader;
    }

    public void setClassLoader(final XmlDeclaration classLoader) {
        this.classLoader = classLoader;
    }

    public XmlDeclaration getTransactionManager() {
        return transactionManager;
    }

    public void setTransactionManager(final XmlDeclaration transactionManager) {
        this.transactionManager = transactionManager;
    }

    public XmlDeclaration getMarshalling() {
        return marshalling;
    }

    public void setMarshalling(final XmlDeclaration marshalling) {
        this.marshalling = marshalling;
    }

    public XmlDeclaration getmBeanServer() {
        return mBeanServer;
    }

    public void setmBeanServer(final XmlDeclaration mBeanServer) {
        this.mBeanServer = mBeanServer;
    }

    public List<XmlDeclaration> getJobLoader() {
        return jobLoader;
    }

    public void setJobLoader(final List<XmlDeclaration> jobLoader) {
        this.jobLoader = jobLoader;
    }

    public List<XmlDeclaration> getArtifactLoader() {
        return artifactLoader;
    }

    public void setArtifactLoader(final List<XmlDeclaration> artifactLoader) {
        this.artifactLoader = artifactLoader;
    }

    public List<XmlDeclaration> getInjector() {
        return injector;
    }

    public void setInjector(final List<XmlDeclaration> injector) {
        this.injector = injector;
    }

    public List<XmlDeclaration> getSecurity() {
        return security;
    }

    public void setSecurity(final List<XmlDeclaration> security) {
        this.security = security;
    }

    public XmlDeclaration getExecutionRepository() {
        return executionRepository;
    }

    public void setExecutionRepository(final XmlDeclaration executionRepository) {
        this.executionRepository = executionRepository;
    }

    public XmlDeclaration getRegistry() {
        return registry;
    }

    public void setRegistry(final XmlDeclaration registry) {
        this.registry = registry;
    }

    public XmlDeclaration getTransport() {
        return transport;
    }

    public void setTransport(final XmlDeclaration transport) {
        this.transport = transport;
    }

    public XmlDeclaration getExecutor() {
        return executor;
    }

    public void setExecutor(final XmlDeclaration executor) {
        this.executor = executor;
    }

    public List<XmlProperty> getProperties() {
        return properties;
    }

    public void setProperties(final List<XmlProperty> properties) {
        this.properties = properties;
    }

    private static String name(final XmlNamed dec, final String def) {
        return dec == null ? def : dec.getName();
    }

    static String ref(final XmlDeclaration dec) {
        return dec == null ? null : dec.getRef();
    }

    public void configureScope(final ScopeModelImpl scope, final ClassLoader classLoader) throws Exception {
        //TODO Do these need to get inserted into the model?
        final Properties properties = XmlProperty.convert(this.properties);
        final JobOperatorModel model = scope.getJobOperator(this.name);

        model.getExecutor()
                .setName(name(this.executor, JobOperatorModelImpl.EXECUTOR))
                .setProperties(properties)
                .setRef(ref(this.executor));
        model.getTransport()
                .setName(name(this.transport, JobOperatorModelImpl.TRANSPORT))
                .setProperties(properties)
                .setRef(ref(this.transport));
        model.getMarshalling()
                .setName(name(this.marshalling, JobOperatorModelImpl.MARSHALLING))
                .setProperties(properties)
                .setRef(ref(this.marshalling));
        model.getRegistry()
                .setName(name(this.registry, JobOperatorModelImpl.REGISTRY))
                .setProperties(properties)
                .setRef(ref(this.registry));
        if (this.mBeanServer != null) {
            model.getMBeanServer() //This is nullable, calling getMBeanServer() will add it to the model
                    .setName(name(this.mBeanServer, JobOperatorModelImpl.MBEAN_SERVER))
                    .setProperties(properties)
                    .setRef(ref(this.mBeanServer));
        }
        model.getExecutionRepository()
                .setName(name(this.executionRepository, JobOperatorModelImpl.EXECUTION_REPOSITORY))
                .setProperties(properties)
                .setRef(ref(this.executionRepository));
        model.getClassLoader()
                .setName(name(this.classLoader, JobOperatorModelImpl.CLASS_LOADER))
                .setProperties(properties)
                .setRef(ref(this.classLoader));
        model.getTransactionManager()
                .setName(name(this.transactionManager, JobOperatorModelImpl.TRANSACTION_MANAGER))
                .setProperties(properties)
                .setRef(ref(this.transactionManager));
        for (final XmlDeclaration resource : this.jobLoader) {
            model.getJobLoader(resource.getName())
                    .setProperties(properties)
                    .setRef(ref(resource));
        }
        for (final XmlDeclaration resource : this.artifactLoader) {
            model.getArtifactLoader(resource.getName())
                    .setProperties(properties)
                    .setRef(ref(resource));
        }
        for (final XmlDeclaration resource : this.injector) {
            model.getInjector(resource.getName())
                    .setProperties(properties)
                    .setRef(ref(resource));
        }
        for (final XmlDeclaration resource : this.security) {
            model.getSecurity(resource.getName())
                    .setProperties(properties)
                    .setRef(ref(resource));
        }
        if (this.ref != null) {
            final JobOperatorConfiguration configuration;
            try {
                configuration = scope.getArtifactLoader().load(this.ref, JobOperatorConfiguration.class, classLoader);
            } catch (final ArtifactOfWrongTypeException e) {
                throw new ConfigurationException("attribute 'ref' must be the fqcn of a class extending " + JobOperatorConfiguration.class.getName(), e); //TODO Message
            }
            configuration.configureJobOperator(model);
        }
    }
}
