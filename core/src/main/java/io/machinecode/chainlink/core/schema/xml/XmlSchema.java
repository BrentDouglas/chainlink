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
package io.machinecode.chainlink.core.schema.xml;

import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.core.schema.DeploymentSchema;
import io.machinecode.chainlink.core.schema.JobOperatorSchema;
import io.machinecode.chainlink.core.schema.PropertySchema;
import io.machinecode.chainlink.core.schema.SubSystemSchema;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Utility to copy an existing schema into XML.</p>
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class XmlSchema {

    /**
     * @see http://www.w3.org/TR/2001/REC-xmlschema-2-20010502/#atomic-vs-list
     * @see http://www.w3.org/TR/2000/WD-xml-2e-20000814#NT-S
     */
    public static final String XML_LIST_DELIMITER = "[ \\r\\n\\t]+";

    public static XmlChainlinkSubSystem xmlSubSystem(final SubSystemSchema<?,?,?> subSystem) throws Exception {
        final XmlChainlinkSubSystem model = new XmlChainlinkSubSystem();
        model.setConfigurationLoaders(subSystem.getConfigurationLoaders());
        for (final JobOperatorSchema<?> dec : subSystem.getJobOperators()) {
            model.getJobOperators().add(xmlJobOperator(dec));
        }
        for (final DeploymentSchema<?,?> dec : subSystem.getDeployments()) {
            model.getDeployments().add(xmlDeployment(dec));
        }
        model.setRef(subSystem.getRef());
        return model;
    }

    public static XmlDeployment xmlDeployment(final DeploymentSchema<?,?> deployment) throws Exception {
        final XmlDeployment model = new XmlDeployment();
        model.setName(deployment.getName());
        model.setConfigurationLoaders(deployment.getConfigurationLoaders());
        for (final JobOperatorSchema<?> dec : deployment.getJobOperators()) {
            model.getJobOperators().add(xmlJobOperator(dec));
        }
        model.setRef(deployment.getRef());
        return model;
    }

    public static XmlJobOperator xmlJobOperator(final JobOperatorSchema<?> op) throws Exception {
        final XmlJobOperator model = new XmlJobOperator();
        model.setName(op.getName());
        model.setProperties(xmlProperties(op.getProperties()));
        model.setExecutor(op.getExecutor());
        model.setTransport(op.getTransport());
        model.setMarshalling(op.getMarshalling());
        model.setRegistry(op.getRegistry());
        model.setMBeanServer(op.getMBeanServer());
        model.setRepository(op.getRepository());
        model.setClassLoader(op.getClassLoader());
        model.setTransactionManager(op.getTransactionManager());
        model.setJobLoaders(op.getJobLoaders());
        model.setArtifactLoaders(op.getArtifactLoaders());
        model.setSecurities(op.getSecurities());
        model.setRef(op.getRef());
        return model;
    }

    private static List<XmlProperty> xmlProperties(final List<? extends PropertySchema> properties) {
        final List<XmlProperty> model = new ArrayList<>(properties.size());
        for (final PropertySchema dec : properties) {
            final XmlProperty xml = new XmlProperty();
            xml.setName(dec.getName());
            xml.setValue(dec.getValue());
            model.add(xml);
        }
        return model;
    }

    public static ByteArrayInputStream writeDeployment(final XmlDeployment that) throws Exception {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            that.write(stream);
            return new ByteArrayInputStream(stream.toByteArray());
        }
    }

    public static ByteArrayInputStream writeSubSystem(final XmlChainlinkSubSystem that) throws Exception {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            that.write(stream);
            return new ByteArrayInputStream(stream.toByteArray());
        }
    }

    public static ByteArrayInputStream writeJobOperator(final XmlJobOperator that) throws Exception {
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            that.write(stream);
            return new ByteArrayInputStream(stream.toByteArray());
        }
    }
}
