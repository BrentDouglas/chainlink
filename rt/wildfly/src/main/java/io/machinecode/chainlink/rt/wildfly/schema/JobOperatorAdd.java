package io.machinecode.chainlink.rt.wildfly.schema;

import io.machinecode.chainlink.core.schema.xml.XmlDeclaration;
import io.machinecode.chainlink.core.schema.xml.XmlJobOperator;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.MutableJobOperatorSchema;
import io.machinecode.chainlink.rt.wildfly.WildFlyConstants;
import io.machinecode.chainlink.rt.wildfly.processor.JobOperatorProcessor;
import org.jboss.as.controller.AbstractAddStepHandler;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.as.controller.ServiceVerificationHandler;
import org.jboss.as.server.AbstractDeploymentChainStep;
import org.jboss.as.server.DeploymentProcessorTarget;
import org.jboss.as.server.deployment.Phase;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.service.ServiceController;

import java.util.List;

import static org.jboss.as.controller.descriptions.ModelDescriptionConstants.OP_ADDR;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
public class JobOperatorAdd extends AbstractAddStepHandler {

    public static final JobOperatorAdd GLOBAL_INSTANCE = new JobOperatorAdd(true);
    public static final JobOperatorAdd DEPLOYMENT_INSTANCE = new JobOperatorAdd(false);

    private final boolean global;

    public JobOperatorAdd(final boolean global) {
        this.global = global;
    }

    @Override
    protected void performRuntime(final OperationContext context, final ModelNode operation, final ModelNode model, final ServiceVerificationHandler verificationHandler, final List<ServiceController<?>> newControllers) throws OperationFailedException {
        final PathAddress address = PathAddress.pathAddress(operation.get(OP_ADDR));
        final String name = address.getLastElement().getValue();

        final XmlJobOperator operator = new XmlJobOperator();
        operator.setName(name);
        operator.setRef(nodeToString(model, WildFlyConstants.REF));

        set(model, WildFlyConstants.CLASS_LOADER, new ClassLoaderDec(operator));
        set(model, WildFlyConstants.EXECUTOR, new ExecutorDec(operator));
        set(model, WildFlyConstants.TRANSPORT, new TransportDec(operator));
        set(model, WildFlyConstants.REGISTRY, new RegistryDec(operator));
        set(model, WildFlyConstants.MARSHALLING, new MarshallingDec(operator));
        set(model, WildFlyConstants.REPOSITORY, new RepositoryDec(operator));
        set(model, WildFlyConstants.TRANSACTION_MANAGER, new TransactionManagerDec(operator));
        final String mBeanServer = nodeToString(model, WildFlyConstants.MBEAN_SERVER, WildFlyConstants.REF);
        if (mBeanServer != null) {
            operator.getMBeanServer().setRef(mBeanServer);
        }
        addListNode(model.get(WildFlyConstants.JOB_LOADER), operator.getJobLoaders());
        addListNode(model.get(WildFlyConstants.ARTIFACT_LOADER), operator.getArtifactLoaders());
        addListNode(model.get(WildFlyConstants.SECURITY), operator.getSecurities());
        addProperties(model.get(WildFlyConstants.PROPERTY), operator);

        context.addStep(new DeployJobOperator(global, name, operator), OperationContext.Stage.RUNTIME);
    }

    public static class DeployJobOperator extends AbstractDeploymentChainStep {

        private final boolean global;
        final String name;
        final JobOperatorSchema<?,?> schema;

        public DeployJobOperator(final boolean global, final String name, final JobOperatorSchema<?,?> schema) {
            this.global = global;
            this.name = name;
            this.schema = schema;
        }

        @Override
        public void execute(final DeploymentProcessorTarget processorTarget) {
            processorTarget.addDeploymentProcessor(WildFlyConstants.SUBSYSTEM_NAME, Phase.POST_MODULE, Phase.POST_MODULE_BATCH_ENVIRONMENT, new JobOperatorProcessor(global, name, schema));
        }
    }


    private static void set(final ModelNode model, final String element, final LazyDec lazy) {
        final ModelNode child = model.get(element);
        if (!child.isDefined()) {
            return;
        }
        final String ref = nodeToString(child, WildFlyConstants.REF);
        if (ref == null || ref.isEmpty()) {
            return;
        }
        final XmlDeclaration dec = lazy.get();
        final String name = nodeToString(child, WildFlyConstants.NAME);
        dec.setName(name);
        dec.setRef(ref);
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

    static void addListNode(final ModelNode root, final List<XmlDeclaration> list) {
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
            final String ns = name.asString();
            XmlDeclaration target = null;
            for (final XmlDeclaration dec : list) {
                if (dec.getName().equals(ns)) {
                    target = dec;
                    break;
                }
            }
            if (target == null) {
                target = new XmlDeclaration();
                target.setName(ns);
                list.add(target);
            }
            target.setRef(nodeToString(node.get(WildFlyConstants.REF)));
        }
    }

    static void addProperties(final ModelNode root, final MutableJobOperatorSchema<?,?> operator) {
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
            final String ns = name.asString();
            final String vs = node.get(WildFlyConstants.VALUE).asString();
            operator.setProperty(ns, vs);
        }
    }

    private interface LazyDec {

        XmlDeclaration get();
    }

    private static class ClassLoaderDec implements LazyDec {
        private final XmlJobOperator operator;

        public ClassLoaderDec(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public XmlDeclaration get() {
            XmlDeclaration x = operator.getClassLoader();
            if (x == null) {
                operator.setClassLoader(x = new XmlDeclaration());
            }
            return x;
        }
    }

    private static class ExecutorDec implements LazyDec {
        private final XmlJobOperator operator;

        public ExecutorDec(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public XmlDeclaration get() {
            XmlDeclaration x = operator.getExecutor();
            if (x == null) {
                operator.setExecutor(x = new XmlDeclaration());
            }
            return x;
        }
    }

    private static class TransportDec implements LazyDec {
        private final XmlJobOperator operator;

        public TransportDec(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public XmlDeclaration get() {
            XmlDeclaration x = operator.getTransport();
            if (x == null) {
                operator.setTransport(x = new XmlDeclaration());
            }
            return x;
        }
    }

    private static class RegistryDec implements LazyDec {
        private final XmlJobOperator operator;

        public RegistryDec(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public XmlDeclaration get() {
            XmlDeclaration x = operator.getRegistry();
            if (x == null) {
                operator.setRegistry(x = new XmlDeclaration());
            }
            return x;
        }
    }

    private static class MarshallingDec implements LazyDec {
        private final XmlJobOperator operator;

        public MarshallingDec(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public XmlDeclaration get() {
            XmlDeclaration x = operator.getMarshalling();
            if (x == null) {
                operator.setMarshalling(x = new XmlDeclaration());
            }
            return x;
        }
    }

    private static class RepositoryDec implements LazyDec {
        private final XmlJobOperator operator;

        public RepositoryDec(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public XmlDeclaration get() {
            XmlDeclaration x = operator.getRepository();
            if (x == null) {
                operator.setRepository(x = new XmlDeclaration());
            }
            return x;
        }
    }

    private static class TransactionManagerDec implements LazyDec {
        private final XmlJobOperator operator;

        public TransactionManagerDec(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public XmlDeclaration get() {
            XmlDeclaration x = operator.getTransactionManager();
            if (x == null) {
                operator.setTransactionManager(x = new XmlDeclaration());
            }
            return x;
        }
    }
}
