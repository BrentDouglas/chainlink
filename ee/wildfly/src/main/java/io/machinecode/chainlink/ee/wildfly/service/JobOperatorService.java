package io.machinecode.chainlink.ee.wildfly.service;

import io.machinecode.chainlink.core.configuration.DeclarationImpl;
import io.machinecode.chainlink.core.configuration.JobOperatorModelImpl;
import io.machinecode.chainlink.core.configuration.ScopeModelImpl;
import io.machinecode.chainlink.core.configuration.SubSystemModelImpl;
import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.ee.wildfly.WildFlyConstants;
import io.machinecode.chainlink.ee.wildfly.WildFlyEnvironment;
import io.machinecode.chainlink.ee.wildfly.configuration.WildFlyConfigurationDefaults;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import org.jboss.dmr.ModelNode;
import org.jboss.logging.Logger;
import org.jboss.msc.service.Service;
import org.jboss.msc.service.ServiceName;
import org.jboss.msc.service.StartContext;
import org.jboss.msc.service.StartException;
import org.jboss.msc.service.StopContext;
import org.jboss.msc.value.InjectedValue;

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
            final ScopeModelImpl scope = global
                    ? this.scope.getValue()
                    : this.scope.getValue().getDeployment();
            final JobOperatorModelImpl op = scope.getJobOperator(name);

            op.getClassLoader()
                    .setFactoryFqcn(nodeToString(model.get(WildFlyConstants.CLASS_LOADER)));
            op.getExecutor()
                    .setFactoryFqcn(nodeToString(model.get(WildFlyConstants.EXECUTOR)));
            op.getTransport()
                    .setFactoryFqcn(nodeToString(model.get(WildFlyConstants.TRANSPORT)));
            op.getRegistry()
                    .setFactoryFqcn(nodeToString(model.get(WildFlyConstants.REGISTRY)));
            op.getMarshalling()
                    .setFactoryFqcn(nodeToString(model.get(WildFlyConstants.MARSHALLING)));
            op.getExecutionRepository()
                    .setFactoryFqcn(nodeToString(model.get(WildFlyConstants.EXECUTION_REPOSITORY)));
            op.getTransactionManager()
                    .setFactoryFqcn(nodeToString(model.get(WildFlyConstants.TRANSACTION_MANAGER)));
            op.getMBeanServer()
                    .setFactoryFqcn(nodeToString(model.get(WildFlyConstants.MBEAN_SERVER)));
            addListNode(model.get(WildFlyConstants.JOB_LOADER), new Target() {
                @Override
                public DeclarationImpl target(final String name) {
                    return op.getJobLoader(name);
                }
            });
            addListNode(model.get(WildFlyConstants.ARTIFACT_LOADER), new Target() {
                @Override
                public DeclarationImpl target(final String name) {
                    return op.getArtifactLoader(name);
                }
            });
            addListNode(model.get(WildFlyConstants.INJECTOR), new Target() {
                @Override
                public DeclarationImpl target(final String name) {
                    return op.getInjector(name);
                }
            });
            addListNode(model.get(WildFlyConstants.SECURITY), new Target() {
                @Override
                public DeclarationImpl target(final String name) {
                    return op.getSecurity(name);
                }
            });

            new WildFlyConfigurationDefaults(loader, transactionManager.getValue())
                    .configureJobOperator(op);

            operator = op.createJobOperator();
            environment.getValue().addOperator(module, name, loader.get(), operator);
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

    static String nodeToString(final ModelNode node) {
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
            target.target(name.asString()).setFactoryFqcn(nodeToString(node.get(WildFlyConstants.FACTORY)));
        }

    }

    private interface Target {
        DeclarationImpl target(final String name);
    }
}
