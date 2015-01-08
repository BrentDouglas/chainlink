package io.machinecode.chainlink.core.configuration;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.spi.configuration.Declaration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.ExecutionRepository;
import io.machinecode.chainlink.spi.security.Security;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorModelImpl implements JobOperatorModel {

    public static final String CLASS_LOADER = "class-loader";
    public static final String MARSHALLING = "marshalling";
    public static final String TRANSPORT = "transport";
    public static final String REGISTRY = "registry";
    public static final String EXECUTION_REPOSITORY = "execution-repository";
    public static final String TRANSACTION_MANAGER = "transaction-manager";
    public static final String EXECUTOR = "executor";
    public static final String MBEAN_SERVER = "mbean-server";

    final String name;
    final ScopeModelImpl scope;
    final Set<String> names = new THashSet<>();
    DeclarationImpl<ClassLoader> classLoader;
    DeclarationImpl<Marshalling> marshalling;
    DeclarationImpl<Registry> registry;
    DeclarationImpl<Transport<?>> transport;
    DeclarationImpl<ExecutionRepository> executionRepository;
    DeclarationImpl<TransactionManager> transactionManager;
    DeclarationImpl<Executor> executor;
    DeclarationImpl<MBeanServer> mBeanServer;
    LinkedHashMap<String, DeclarationImpl<JobLoader>> jobLoaders = new LinkedHashMap<>();
    LinkedHashMap<String, DeclarationImpl<ArtifactLoader>> artifactLoaders = new LinkedHashMap<>();
    LinkedHashMap<String, DeclarationImpl<Injector>> injectors = new LinkedHashMap<>();
    LinkedHashMap<String, DeclarationImpl<Security>> securities = new LinkedHashMap<>();
    Properties properties;

    final WeakReference<ClassLoader> loader;
    final Map<String, DeclarationImpl<?>> values = new THashMap<>();

    public JobOperatorModelImpl(final String name, final ScopeModelImpl scope, final WeakReference<ClassLoader> loader) {
        this.loader = loader;
        this.scope = scope;
        this.name = name;
    }

    private JobOperatorModelImpl(final JobOperatorModelImpl that, final ScopeModelImpl scope) {
        this(that.name, scope, that.loader);
        this.names.addAll(that.names);
        this.classLoader = _copyDec(that.classLoader);
        this.marshalling = _copyDec(that.marshalling);
        this.registry = _copyDec(that.registry);
        this.transport = _copyDec(that.transport);
        this.executionRepository = _copyDec(that.executionRepository);
        this.transactionManager = _copyDec(that.transactionManager);
        this.executor = _copyDec(that.executor);
        this.mBeanServer = _copyDec(that.mBeanServer);
        _copyAllDecs(this.jobLoaders, that.jobLoaders);
        _copyAllDecs(this.artifactLoaders, that.artifactLoaders);
        _copyAllDecs(this.injectors, that.injectors);
        _copyAllDecs(this.securities, that.securities);
    }

    public JobOperatorModelImpl copy(final ScopeModelImpl scope) {
        return new JobOperatorModelImpl(this, scope);
    }

    private <T> DeclarationImpl<T> _copyDec(final DeclarationImpl<T> that) {
        return that == null ? null : that.copy(names, values);
    }

    private <T> void _copyAllDecs(final Map<String, DeclarationImpl<T>> to, final Map<String, DeclarationImpl<T>> from) {
        for (final Map.Entry<String, DeclarationImpl<T>> entry : from.entrySet()) {
            to.put(entry.getKey(), entry.getValue().copy(names, values));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> DeclarationImpl<T> getValue(final String name, final Class<?> clazz) {
        final DeclarationImpl<?> value = values.get(name);
        if (value == null) {
            return null;
        }
        if (!value.is(clazz)) {
            throw new IllegalStateException("Resource " + name + " is not of type "+ clazz.getName()); //TODO Message
        }
        return (DeclarationImpl<T>)value;
    }

    protected <T> DeclarationImpl<T> createValue(final String name, final Class<?> clazz) {
        if (names.contains(name)) {
            throw new IllegalStateException("Resource already declared for name: " + name); //TODO Message and better exception
        }
        return new DeclarationImpl<>(loader, names, values, clazz, name);
    }

    @Override
    public DeclarationImpl<ClassLoader> getClassLoader() {
        if (classLoader == null) {
            return classLoader = new DeclarationImpl<>(loader, names, values, ClassLoader.class, CLASS_LOADER);
        }
        return classLoader;
    }

    @Override
    public DeclarationImpl<Marshalling> getMarshalling() {
        if (marshalling == null) {
            return marshalling = new DeclarationImpl<>(loader, names, values, Marshalling.class, MARSHALLING);
        }
        return marshalling;
    }

    @Override
    public Declaration<Transport<?>> getTransport() {
        if (transport == null) {
            return transport = new DeclarationImpl<>(loader, names, values, Transport.class, TRANSPORT);
        }
        return transport;
    }

    @Override
    public DeclarationImpl<Registry> getRegistry() {
        if (registry == null) {
            return registry = new DeclarationImpl<>(loader, names, values, Registry.class, REGISTRY);
        }
        return registry;
    }

    @Override
    public DeclarationImpl<ExecutionRepository> getExecutionRepository() {
        if (executionRepository == null) {
            return executionRepository = new DeclarationImpl<>(loader, names, values, ExecutionRepository.class, EXECUTION_REPOSITORY);
        }
        return executionRepository;
    }

    @Override
    public DeclarationImpl<TransactionManager> getTransactionManager() {
        if (transactionManager == null) {
            return transactionManager = new DeclarationImpl<>(loader, names, values, TransactionManager.class, TRANSACTION_MANAGER);
        }
        return transactionManager;
    }

    @Override
    public DeclarationImpl<Executor> getExecutor() {
        if (executor == null) {
            return executor = new DeclarationImpl<>(loader, names, values, Executor.class, EXECUTOR);
        }
        return executor;
    }

    @Override
    public DeclarationImpl<MBeanServer> getMBeanServer() {
        if (mBeanServer == null) {
            return mBeanServer = new DeclarationImpl<>(loader, names, values, MBeanServer.class, MBEAN_SERVER);
        }
        return mBeanServer;
    }

    @Override
    public DeclarationImpl<JobLoader> getJobLoader(final String name) {
        DeclarationImpl<JobLoader> jobLoader = jobLoaders.get(name);
        if (jobLoader != null) {
            return jobLoader;
        }
        jobLoaders.put(name, jobLoader = createValue(name, JobLoader.class));
        return jobLoader;
    }

    @Override
    public DeclarationImpl<ArtifactLoader> getArtifactLoader(final String name) {
        DeclarationImpl<ArtifactLoader> artifactLoader = artifactLoaders.get(name);
        if (artifactLoader != null) {
            return artifactLoader;
        }
        artifactLoaders.put(name, artifactLoader = createValue(name, ArtifactLoader.class));
        return artifactLoader;
    }

    @Override
    public DeclarationImpl<Injector> getInjector(final String name) {
        DeclarationImpl<Injector> injector = injectors.get(name);
        if (injector != null) {
            return injector;
        }
        injectors.put(name, injector = createValue(name, Injector.class));
        return injector;
    }

    @Override
    public DeclarationImpl<Security> getSecurity(final String name) {
        DeclarationImpl<Security> security = securities.get(name);
        if (security != null) {
            return security;
        }
        securities.put(name, security = createValue(name, Security.class));
        return security;
    }

    @Override
    public Properties getProperties() {
        return this.properties == null
                ? this.properties = new Properties()
                : this.properties;
    }

    public JobOperatorImpl createJobOperator() throws Exception {
        final ConfigurationImpl configuration = scope.getConfiguration(name);
        final JobOperatorImpl op = new JobOperatorImpl(configuration, getProperties());
        op.open(configuration);
        return op;
    }
}
