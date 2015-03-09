package io.machinecode.chainlink.rt.wildfly.service;

import io.machinecode.chainlink.core.configuration.ClassLoaderFactoryImpl;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.ScopeModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.execution.EventedExecutorFactory;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.core.management.jmx.PlatformMBeanServerFactory;
import io.machinecode.chainlink.core.registry.LocalRegistryFactory;
import io.machinecode.chainlink.core.repository.memory.MemoryRepositoryFactory;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.configuration.Model;
import io.machinecode.chainlink.core.transaction.ReferenceTransactionManagerFactory;
import io.machinecode.chainlink.core.transport.LocalTransportFactory;
import io.machinecode.chainlink.marshalling.jboss.JbossMarshallingFactory;
import io.machinecode.chainlink.rt.wildfly.WildFlyEnvironment;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

import javax.enterprise.inject.spi.BeanManager;
import javax.transaction.TransactionManager;
import java.lang.ref.WeakReference;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobOperatorService implements Service<ExtendedJobOperator> {

    private static final Logger log = Logger.getLogger(JobOperatorService.class);

    private final ServiceName module;
    private final WeakReference<ClassLoader> loader;

    private final boolean global;
    private final String name;
    private final JobOperatorSchema<?> schema;

    private JobOperatorImpl operator;

    private final InjectedValue<WildFlyEnvironment> environment = new InjectedValue<>();
    private final InjectedValue<TransactionManager> transactionManager = new InjectedValue<>();
    private final InjectedValue<SubSystemModelImpl> scope = new InjectedValue<>();
    private final InjectedValue<BeanManager> beanManager = new InjectedValue<>();

    public JobOperatorService(final ServiceName module, final ClassLoader loader, final String name, final boolean global, final JobOperatorSchema<?> schema) {
        this.module = module;
        this.loader = new WeakReference<>(loader);
        this.global = global;
        this.name = name;
        this.schema = schema;
    }

    @Override
    public void start(final StartContext context) throws StartException {
        try {
            final ClassLoader classLoader = loader.get();
            if (classLoader == null) {
                throw new StartException(); //TODO Message
            }
            final DeploymentModelImpl deployment;
            final ScopeModelImpl scope;
            if (!global) {
                scope = deployment = this.scope.getValue().findDeployment(Constants.DEFAULT); //TODO This needs to be the module name
            } else {
                scope = this.scope.getValue();
                deployment = null;
            }

            final JobOperatorModelImpl operator = scope.getJobOperator(name);

            configureJobOperator(operator, loader, transactionManager.getValue());

            Model.configureJobOperator(scope, this.schema, classLoader);

            final ConfigurationLoader art = new WildFlyConfigurationLoader(beanManager.getOptionalValue());

            final JobOperatorModelImpl model;
            if (!global) {
                final DeploymentModelImpl dep = deployment.copy(classLoader);
                final JobOperatorModelImpl op = dep.getJobOperator(name);
                dep.loadChainlinkXml();
                model = op;
            } else {
                model = operator;
            }
            final Thread thread = Thread.currentThread();
            final ClassLoader tccl = thread.getContextClassLoader();
            thread.setContextClassLoader(classLoader);
            try {
                this.operator = model.createJobOperator(art);
            } finally {
                thread.setContextClassLoader(tccl);
            }
            environment.getValue().addOperator(module, name, loader.get(), this.operator);
        } catch (final Exception e) {
            throw new StartException(e);
        }
    }

    @Override
    public void stop(final StopContext context) {
        try {
            operator.close();
        } catch (final Exception e) {
            log.error("Error while stopping JobOperator", e); //TODO Message
        } finally {
            try {
                environment.getValue().removeOperator(module, name);
            } catch (final Exception e) {
                log.error("Error while removing JobOperator", e); //TODO Message
            } finally {
                operator = null;
            }
        }
    }

    @Override
    public ExtendedJobOperator getValue() throws IllegalStateException, IllegalArgumentException {
        return operator;
    }

    public InjectedValue<WildFlyEnvironment> getEnvironment() {
        return environment;
    }

    public InjectedValue<TransactionManager> getTransactionManager() {
        return transactionManager;
    }

    public InjectedValue<SubSystemModelImpl> getScope() {
        return scope;
    }

    public InjectedValue<BeanManager> getBeanManager() {
        return beanManager;
    }

    private static void configureJobOperator(final JobOperatorModel model, final WeakReference<ClassLoader> loader, final TransactionManager transactionManager) throws Exception {
        model.getClassLoader().setDefaultFactory(new ClassLoaderFactoryImpl(loader));
        model.getTransactionManager().setDefaultFactory(new ReferenceTransactionManagerFactory(transactionManager));
        model.getRepository().setDefaultFactory(new MemoryRepositoryFactory());
        model.getMarshalling().setDefaultFactory(new JbossMarshallingFactory());
        model.getMBeanServer().setDefaultFactory(new PlatformMBeanServerFactory());
        model.getTransport().setDefaultFactory(new LocalTransportFactory());
        model.getRegistry().setDefaultFactory(new LocalRegistryFactory());
        model.getExecutor().setDefaultFactory(new EventedExecutorFactory());
    }
}
