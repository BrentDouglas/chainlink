/*
 * Copyright 2015 Brent Douglas and other contributors
 * as indicated by the @author tags. All rights reserved.
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
package io.machinecode.chainlink.rt.wildfly.schema;

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

import java.util.ArrayList;
import java.util.Collections;
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

        set(model, WildFlyConstants.CLASS_LOADER, new SetClassLoader(operator));
        set(model, WildFlyConstants.EXECUTOR, new SetExecutor(operator));
        set(model, WildFlyConstants.TRANSPORT, new SetTransport(operator));
        set(model, WildFlyConstants.REGISTRY, new SetRegistry(operator));
        set(model, WildFlyConstants.MARSHALLING, new SetMarshalling(operator));
        set(model, WildFlyConstants.REPOSITORY, new SetRepository(operator));
        set(model, WildFlyConstants.TRANSACTION_MANAGER, new SetTransactionManager(operator));
        final String mBeanServer = nodeToString(model, WildFlyConstants.MBEAN_SERVER);
        if (mBeanServer != null) {
            operator.setMBeanServer(mBeanServer);
        }
        operator.setJobLoaders(getListNode(model.get(WildFlyConstants.JOB_LOADERS)));
        operator.setArtifactLoaders(getListNode(model.get(WildFlyConstants.ARTIFACT_LOADERS)));
        operator.setSecurities(getListNode(model.get(WildFlyConstants.SECURITIES)));
        addProperties(model.get(WildFlyConstants.PROPERTY), operator);

        context.addStep(new DeployJobOperator(global, name, operator), OperationContext.Stage.RUNTIME);
    }

    public static class DeployJobOperator extends AbstractDeploymentChainStep {

        private final boolean global;
        final String name;
        final JobOperatorSchema<?> schema;

        public DeployJobOperator(final boolean global, final String name, final JobOperatorSchema<?> schema) {
            this.global = global;
            this.name = name;
            this.schema = schema;
        }

        @Override
        public void execute(final DeploymentProcessorTarget processorTarget) {
            processorTarget.addDeploymentProcessor(WildFlyConstants.SUBSYSTEM_NAME, Phase.POST_MODULE, Phase.POST_MODULE_BATCH_ENVIRONMENT, new JobOperatorProcessor(global, name, schema));
        }
    }


    private static void set(final ModelNode model, final String element, final Set lazy) {
        final ModelNode child = model.get(element);
        if (!child.isDefined()) {
            return;
        }
        final String ref = nodeToString(child);
        if (ref == null || ref.isEmpty()) {
            return;
        }
        lazy.set(ref);
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

    static List<String> getListNode(final ModelNode root) {
        if (!root.isDefined()) {
            return Collections.emptyList();
        }
        final List<ModelNode> nodes = root.asList();
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }
        final List<String> ret = new ArrayList<>(nodes.size());
        for (final ModelNode node : nodes) {
            final ModelNode name = node.get(WildFlyConstants.NAME);
            if (!name.isDefined()) {
                throw new IllegalStateException(); //TODO Message
            }
            ret.add(name.asString());
        }
        return ret;
    }

    static void addProperties(final ModelNode root, final MutableJobOperatorSchema<?> operator) {
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

    private interface Set {

        void set(final String value);
    }

    private static class SetClassLoader implements Set {
        private final XmlJobOperator operator;

        public SetClassLoader(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public void set(final String value) {
            operator.setClassLoader(value);
        }
    }

    private static class SetExecutor implements Set {
        private final XmlJobOperator operator;

        public SetExecutor(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public void set(final String value) {
            operator.setExecutor(value);
        }
    }

    private static class SetTransport implements Set {
        private final XmlJobOperator operator;

        public SetTransport(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public void set(final String value) {
            operator.setTransport(value);
        }
    }

    private static class SetRegistry implements Set {
        private final XmlJobOperator operator;

        public SetRegistry(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public void set(final String value) {
            operator.setRegistry(value);
        }
    }

    private static class SetMarshalling implements Set {
        private final XmlJobOperator operator;

        public SetMarshalling(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public void set(final String value) {
            operator.setMarshalling(value);
        }
    }

    private static class SetRepository implements Set {
        private final XmlJobOperator operator;

        public SetRepository(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public void set(final String value) {
            operator.setRepository(value);
        }
    }

    private static class SetTransactionManager implements Set {
        private final XmlJobOperator operator;

        public SetTransactionManager(final XmlJobOperator operator) {
            this.operator = operator;
        }

        @Override
        public void set(final String value) {
            operator.setTransactionManager(value);
        }
    }
}
