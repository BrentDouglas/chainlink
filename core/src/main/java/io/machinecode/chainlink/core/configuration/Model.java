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
package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.core.schema.DeploymentSchema;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.PropertySchema;
import io.machinecode.chainlink.core.schema.ScopeSchema;
import io.machinecode.chainlink.core.schema.SubSystemSchema;
import io.machinecode.chainlink.spi.configuration.Declaration;
import io.machinecode.chainlink.spi.configuration.DeploymentConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorConfiguration;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.spi.configuration.PropertyModel;
import io.machinecode.chainlink.spi.configuration.SubSystemConfiguration;
import io.machinecode.chainlink.spi.exception.ConfigurationException;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;

import java.util.List;

/**
 * <p>Utility to configure a model from a schema.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class Model {

    public static void configureSubSystem(final SubSystemModelImpl model, final SubSystemSchema<?,?,?> subSystem, final ClassLoader classLoader) throws Exception {
        configureScope(model, subSystem, classLoader);
        for (final DeploymentSchema<?,?> dec : subSystem.getDeployments()) {
            configureDeployment(model.getDeployment(dec.getName()), dec, classLoader);
        }
        if (subSystem.getRef() != null) {
            final SubSystemConfiguration configuration;
            try {
                configuration = model.getConfigurationLoader().load(subSystem.getRef(), SubSystemConfiguration.class, classLoader);
            } catch (final Exception e) {
                throw new ConfigurationException("attribute 'ref' must be an injectable " + SubSystemConfiguration.class.getName(), e); //TODO Message
            }
            if (configuration == null) {
                throw new ConfigurationException("No " + SubSystemConfiguration.class.getName() + " with ref " + subSystem.getRef()); //TODO Message
            }
            configuration.configureSubSystem(model);
        }
    }

    public static void configureDeployment(final DeploymentModelImpl model, final DeploymentSchema<?,?> deployment, final ClassLoader classLoader) throws Exception {
        configureScope(model, deployment, classLoader);
        if (deployment.getRef() != null) {
            final DeploymentConfiguration configuration;
            try {
                configuration = model.getConfigurationLoader().load(deployment.getRef(), DeploymentConfiguration.class, classLoader);
            } catch (final Exception e) {
                throw new ConfigurationException("attribute 'ref' must be an injectable " + DeploymentConfiguration.class.getName(), e); //TODO Message
            }
            if (configuration == null) {
                throw new ConfigurationException("No " + DeploymentConfiguration.class.getName() + " with ref " + deployment.getRef()); //TODO Message
            }
            configuration.configureDeployment(model);
        }
    }

    private static void configureScope(final ScopeModelImpl model, final ScopeSchema<?,?> schema, final ClassLoader classLoader) throws Exception {
        properties(schema.getProperties(), model);
        model.getConfigurationLoaders().set(schema.getConfigurationLoaders());
        for (final JobOperatorSchema<?> dec : schema.getJobOperators()) {
            configureJobOperator(model, dec, classLoader);
        }
    }

    public static void configureJobOperator(final ScopeModelImpl scope, final JobOperatorSchema<?> op, final ClassLoader classLoader) throws Exception {
        final JobOperatorModel model = scope.getJobOperator(op.getName());

        properties(op.getProperties(), model);

        set(model.getExecutor(), op.getExecutor());
        set(model.getTransport(), op.getTransport());
        set(model.getMarshalling(), op.getMarshalling());
        set(model.getRegistry(), op.getRegistry());
        if (op.getMBeanServer() != null) {
            model.getMBeanServer() //This is nullable, calling getMBeanServer() will add it to the model
                    .setRef(op.getMBeanServer());
        }
        set(model.getRepository(), op.getRepository());
        set(model.getClassLoader(), op.getClassLoader());
        set(model.getTransactionManager(), op.getTransactionManager());
        model.getJobLoaders().set(op.getJobLoaders());
        model.getArtifactLoaders().set(op.getArtifactLoaders());
        model.getSecurities().set(op.getSecurities());
        if (op.getRef() != null) {
            final JobOperatorConfiguration configuration;
            try {
                configuration = scope.getConfigurationLoader().load(op.getRef(), JobOperatorConfiguration.class, classLoader);
            } catch (final ArtifactOfWrongTypeException e) {
                throw new ConfigurationException("attribute 'ref' must be the fqcn of a class extending " + JobOperatorConfiguration.class.getName(), e); //TODO Message
            }
            if (configuration == null) {
                throw new ConfigurationException("No " + JobOperatorConfiguration.class.getName() + " with ref " + op.getRef()); //TODO Message
            }
            configuration.configureJobOperator(model);
        }
    }

    private static void properties(final List<? extends PropertySchema> properties, final PropertyModel target) {
        for (final PropertySchema property : properties) {
            target.setProperty(property.getName(), property.getValue());
        }
    }

    private static void set(final Declaration<?> dec, final String ref) {
        if (ref == null || ref.trim().isEmpty()) {
            return;
        }
        dec.setRef(ref);
    }
}
