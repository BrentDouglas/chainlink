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
package io.machinecode.chainlink.core.configuration;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.management.LazyJobOperator;
import io.machinecode.chainlink.core.property.ArrayPropertyLookup;
import io.machinecode.chainlink.core.property.SystemPropertyLookup;
import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.factory.ArtifactLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.ExecutorFactory;
import io.machinecode.chainlink.spi.configuration.factory.JobLoaderFactory;
import io.machinecode.chainlink.spi.configuration.factory.MBeanServerFactory;
import io.machinecode.chainlink.spi.configuration.factory.MarshallingFactory;
import io.machinecode.chainlink.spi.configuration.factory.RegistryFactory;
import io.machinecode.chainlink.spi.configuration.factory.RepositoryFactory;
import io.machinecode.chainlink.spi.configuration.factory.SecurityFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransactionManagerFactory;
import io.machinecode.chainlink.spi.configuration.factory.TransportFactory;
import io.machinecode.chainlink.spi.execution.Executor;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.marshalling.Marshalling;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.registry.Registry;
import io.machinecode.chainlink.spi.repository.Repository;
import io.machinecode.chainlink.spi.security.Security;
import io.machinecode.chainlink.spi.transport.Transport;

import javax.management.MBeanServer;
import javax.transaction.TransactionManager;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorModelImpl extends PropertyModelImpl implements JobOperatorModel {

    final String name;
    final ScopeModelImpl scope;
    final Set<String> names = new THashSet<>();
    DeclarationImpl<ClassLoader> classLoader;
    DeclarationImpl<Marshalling> marshalling;
    DeclarationImpl<Registry> registry;
    DeclarationImpl<Transport> transport;
    DeclarationImpl<Repository> repository;
    DeclarationImpl<TransactionManager> transactionManager;
    DeclarationImpl<Executor> executor;
    DeclarationImpl<MBeanServer> mBeanServer;
    final ListModelImpl<JobLoader> jobLoaders = new ListModelImpl<JobLoader>() {
        @Override
        protected DeclarationImpl<JobLoader> create() {
            return new DeclarationImpl<>(loader, JobLoader.class, JobLoaderFactory.class);
        }
    };
    final ListModelImpl<ArtifactLoader> artifactLoaders = new ListModelImpl<ArtifactLoader>() {
        @Override
        protected DeclarationImpl<ArtifactLoader> create() {
            return new DeclarationImpl<>(loader, ArtifactLoader.class, ArtifactLoaderFactory.class);
        }
    };
    final ListModelImpl<Security> securities = new ListModelImpl<Security>() {
        @Override
        protected DeclarationImpl<Security> create() {
            return new DeclarationImpl<>(loader, Security.class, SecurityFactory.class);
        }
    };

    final PropertyLookup configuredProperties;
    final SystemPropertyLookup systemProperties;

    final WeakReference<ClassLoader> loader;
    final Map<String, DeclarationImpl<?>> values = new THashMap<>();

    public JobOperatorModelImpl(final String name, final ScopeModelImpl scope, final WeakReference<ClassLoader> loader) {
        this.loader = loader;
        this.scope = scope;
        this.name = name;
        if (scope.parent == null) {
            this.configuredProperties = new ArrayPropertyLookup(this.properties, scope.properties);
        } else {
            //TODO Maybe make this more generic
            this.configuredProperties = new ArrayPropertyLookup(this.properties, scope.properties, scope.parent.properties);
        }
        this.systemProperties = new SystemPropertyLookup(this.configuredProperties);
    }

    private JobOperatorModelImpl(final JobOperatorModelImpl that, final ScopeModelImpl scope) {
        this(that.name, scope, that.loader);
        this.names.addAll(that.names);
        this.classLoader = _copyDec(that.classLoader);
        this.marshalling = _copyDec(that.marshalling);
        this.registry = _copyDec(that.registry);
        this.transport = _copyDec(that.transport);
        this.repository = _copyDec(that.repository);
        this.transactionManager = _copyDec(that.transactionManager);
        this.executor = _copyDec(that.executor);
        this.mBeanServer = _copyDec(that.mBeanServer);
        _copyAllDecs(this.jobLoaders, that.jobLoaders);
        _copyAllDecs(this.artifactLoaders, that.artifactLoaders);
        _copyAllDecs(this.securities, that.securities);
    }

    public JobOperatorModelImpl copy(final ScopeModelImpl scope) {
        return new JobOperatorModelImpl(this, scope);
    }

    private <T> DeclarationImpl<T> _copyDec(final DeclarationImpl<T> that) {
        return that == null ? null : that.copy();
    }

    private <T> void _copyAllDecs(final ListModelImpl<T> to, final ListModelImpl<T> from) {
        for (final DeclarationImpl<T> entry : from) {
            to.add(entry.copy());
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

    @Override
    public DeclarationImpl<ClassLoader> getClassLoader() {
        if (classLoader == null) {
            return classLoader = new DeclarationImpl<>(loader, ClassLoader.class, ClassLoaderFactory.class);
        }
        return classLoader;
    }

    @Override
    public DeclarationImpl<Marshalling> getMarshalling() {
        if (marshalling == null) {
            return marshalling = new DeclarationImpl<>(loader, Marshalling.class, MarshallingFactory.class);
        }
        return marshalling;
    }

    @Override
    public DeclarationImpl<Transport> getTransport() {
        if (transport == null) {
            return transport = new DeclarationImpl<>(loader, Transport.class, TransportFactory.class);
        }
        return transport;
    }

    @Override
    public DeclarationImpl<Registry> getRegistry() {
        if (registry == null) {
            return registry = new DeclarationImpl<>(loader, Registry.class, RegistryFactory.class);
        }
        return registry;
    }

    @Override
    public DeclarationImpl<Repository> getRepository() {
        if (repository == null) {
            return repository = new DeclarationImpl<>(loader, Repository.class, RepositoryFactory.class);
        }
        return repository;
    }

    @Override
    public DeclarationImpl<TransactionManager> getTransactionManager() {
        if (transactionManager == null) {
            return transactionManager = new DeclarationImpl<>(loader, TransactionManager.class, TransactionManagerFactory.class);
        }
        return transactionManager;
    }

    @Override
    public DeclarationImpl<Executor> getExecutor() {
        if (executor == null) {
            return executor = new DeclarationImpl<>(loader, Executor.class, ExecutorFactory.class);
        }
        return executor;
    }

    @Override
    public DeclarationImpl<MBeanServer> getMBeanServer() {
        if (mBeanServer == null) {
            return mBeanServer = new DeclarationImpl<>(loader, MBeanServer.class, MBeanServerFactory.class);
        }
        return mBeanServer;
    }

    @Override
    public ListModelImpl<JobLoader> getJobLoaders() {
        return jobLoaders;
    }

    @Override
    public ListModelImpl<ArtifactLoader> getArtifactLoaders() {
        return artifactLoaders;
    }

    @Override
    public ListModelImpl<Security> getSecurities() {
        return securities;
    }

    public PropertyLookup getProperties() {
        return configuredProperties;
    }

    public JobOperatorImpl createJobOperator(final ConfigurationLoader loader) throws Exception {
        final ConfigurationImpl configuration = scope.getConfiguration(name, loader);
        final JobOperatorImpl op = new JobOperatorImpl(configuration, this.systemProperties);
        op.open(configuration);
        return op;
    }

    public JobOperatorImpl createJobOperator() throws Exception {
        final ConfigurationImpl configuration = scope.getConfiguration(name);
        final JobOperatorImpl op = new JobOperatorImpl(configuration, this.systemProperties);
        op.open(configuration);
        return op;
    }

    public LazyJobOperator createLazyJobOperator(final ConfigurationLoader loader) throws Exception {
        return new LazyJobOperator(this, loader);
    }

    public LazyJobOperator createLazyJobOperator() throws Exception {
        return new LazyJobOperator(this);
    }

    public ConfigurationImpl getConfiguration() throws Exception {
        return scope.getConfiguration(name);
    }
}
