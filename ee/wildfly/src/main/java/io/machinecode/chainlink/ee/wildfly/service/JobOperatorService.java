package io.machinecode.chainlink.ee.wildfly.service;

import io.machinecode.chainlink.core.configuration.DeclarationImpl;
import io.machinecode.chainlink.core.configuration.DeploymentModelImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.ScopeModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import io.machinecode.chainlink.ee.wildfly.WildFlyEnvironment;
import io.machinecode.chainlink.ee.wildfly.configuration.WildFlyConfigurationDefaults;
import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import org.jboss.dmr.ModelNode;
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
import java.util.List;

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
    private final ModelNode model;

    private JobOperatorImpl operator;

    private final InjectedValue<WildFlyEnvironment> environment = new InjectedValue<>();
    private final InjectedValue<TransactionManager> transactionManager = new InjectedValue<>();
    private final InjectedValue<SubSystemModelImpl> scope = new InjectedValue<>();
    private final InjectedValue<BeanManager> beanManager = new InjectedValue<>();

    public JobOperatorService(final ServiceName module, final ClassLoader loader, final String name, final boolean global, final ModelNode model) {
        this.module = module;
        this.loader = new WeakReference<>(loader);
        this.global = global;
        this.name = name;
        this.model = model;
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

            set(operator.getClassLoader(), nodeToString(model, WildFlyConstants.CLASS_LOADER, WildFlyConstants.REF));
            set(operator.getExecutor(), nodeToString(model, WildFlyConstants.EXECUTOR, WildFlyConstants.REF));
            set(operator.getTransport(), nodeToString(model, WildFlyConstants.TRANSPORT, WildFlyConstants.REF));
            set(operator.getRegistry(), nodeToString(model, WildFlyConstants.REGISTRY, WildFlyConstants.REF));
            set(operator.getMarshalling(), nodeToString(model, WildFlyConstants.MARSHALLING, WildFlyConstants.REF));
            set(operator.getExecutionRepository(), nodeToString(model, WildFlyConstants.EXECUTION_REPOSITORY, WildFlyConstants.REF));
            set(operator.getTransactionManager(), nodeToString(model, WildFlyConstants.TRANSACTION_MANAGER, WildFlyConstants.REF));
            final String mBeanServer = nodeToString(model, WildFlyConstants.MBEAN_SERVER, WildFlyConstants.REF);
            if (mBeanServer != null) {
                operator.getMBeanServer().setRef(mBeanServer);
            }
            addListNode(model.get(WildFlyConstants.JOB_LOADER), new Target() {
                @Override
                public DeclarationImpl target(final String name) {
                    return operator.getJobLoader(name);
                }
            });
            addListNode(model.get(WildFlyConstants.ARTIFACT_LOADER), new Target() {
                @Override
                public DeclarationImpl target(final String name) {
                    return operator.getArtifactLoader(name);
                }
            });
            addListNode(model.get(WildFlyConstants.INJECTOR), new Target() {
                @Override
                public DeclarationImpl target(final String name) {
                    return operator.getInjector(name);
                }
            });
            addListNode(model.get(WildFlyConstants.SECURITY), new Target() {
                @Override
                public DeclarationImpl target(final String name) {
                    return operator.getSecurity(name);
                }
            });
            addProperties(model.get(WildFlyConstants.PROPERTY), operator);

            new WildFlyConfigurationDefaults(loader, transactionManager.getValue())
                    .configureJobOperator(operator);

            final ArtifactLoader art = new WildFlyArtifactLoader(beanManager.getOptionalValue());

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

    private static void set(final DeclarationImpl<?> dec, final String ref) {
        if (ref == null || ref.isEmpty()) {
            return;
        }
        dec.setRef(ref);
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

    static String nodeToString(ModelNode node, final String... path) {
        for (final String part : path) {
            node = node.get(part);
            if (!node.isDefined()) {
                return null;
            }
        }
        return node.isDefined()
                ? node.asString()
                : null;
    }

    static void addListNode(final ModelNode root, final Target target) {
        if (!root.isDefined()) {
            return;
        }
        final List<ModelNode> nodes = root.asList();
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        for (final ModelNode node : nodes) {
            final ModelNode name = node.get(WildFlyConstants.NAME);
            if (!name.isDefined()) {
                throw new IllegalStateException(); //TODO Message
            }
            target.target(name.asString()).setRef(nodeToString(node.get(WildFlyConstants.REF)));
        }
    }

    static void addProperties(final ModelNode root, final JobOperatorModelImpl operator) {
        if (!root.isDefined()) {
            return;
        }
        final List<ModelNode> nodes = root.asList();
        if (nodes == null || nodes.isEmpty()) {
            return;
        }
        for (final ModelNode node : nodes) {
            final ModelNode name = node.get(WildFlyConstants.NAME);
            if (!name.isDefined()) {
                throw new IllegalStateException(); //TODO Message
            }
            operator.getRawProperties().setProperty(name.asString(), node.get(WildFlyConstants.VALUE).asString());
        }
    }

    private interface Target {
        DeclarationImpl target(final String name);
    }
}
