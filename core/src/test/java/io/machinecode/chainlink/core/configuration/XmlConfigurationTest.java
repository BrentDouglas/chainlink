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

import io.machinecode.chainlink.core.schema.xml.XmlChainlink;
import io.machinecode.chainlink.core.schema.xml.XmlDeployment;
import io.machinecode.chainlink.core.schema.xml.XmlJobOperator;
import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.core.util.Op;
import io.machinecode.chainlink.core.util.Tccl;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class XmlConfigurationTest extends Assert {

    @Test
    public void subsystemTest() throws Exception {
        final XmlChainlinkSubSystem that = XmlChainlinkSubSystem.read(Tccl.get().getResourceAsStream("test/xml/subsystem.xml"));
        assertNotNull(that);
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());
        assertEquals(2, that.getDeployments().size());
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void subsystemAcceptTest() throws Exception {
        final XmlChainlinkSubSystem that = XmlChainlinkSubSystem.read(Tccl.get().getResourceAsStream("test/xml/subsystem.xml"));
        assertEquals("theSubSystemConfiguration", that.getRef());
        that.accept(new XmlChainlinkSubSystem());
        assertNull(that.getRef());
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());
        assertEquals(2, that.getDeployments().size());

        that.accept(new XmlChainlinkSubSystem(), Op.values());
        assertNull(that.getRef());
        assertEquals(0, that.getConfigurationLoaders().size());
        assertEquals(0, that.getJobOperators().size());
        assertEquals(0, that.getDeployments().size());

        that.accept(XmlChainlinkSubSystem.read(Tccl.get().getResourceAsStream("test/xml/subsystem.xml")), Op.values());
        assertEquals("theSubSystemConfiguration", that.getRef());
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());
        assertEquals(2, that.getDeployments().size());
    }

    @Test
    public void chainlinkTest() throws Exception {
        final XmlChainlink that = XmlChainlink.read(Tccl.get().getResourceAsStream("test/configuration/chainlink.xml"));
        assertNotNull(that);
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void chainlinkAcceptTest() throws Exception {
        final XmlChainlink that = XmlChainlink.read(Tccl.get().getResourceAsStream("test/configuration/chainlink.xml"));
        final XmlChainlink xml = new XmlChainlink();
        xml.setRef("asdf");
        that.accept(xml);
        assertEquals("asdf", that.getRef());
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());

        that.accept(new XmlChainlink(), Op.values());
        assertNull(that.getRef());
        assertEquals(0, that.getConfigurationLoaders().size());
        assertEquals(0, that.getJobOperators().size());

        that.accept(XmlChainlink.read(Tccl.get().getResourceAsStream("test/configuration/chainlink.xml")), Op.values());
        assertNull(that.getRef());
        assertEquals(1, that.getConfigurationLoaders().size());
        assertEquals(1, that.getJobOperators().size());
    }

    @Test
    public void deploymentTest() throws Exception {
        final XmlDeployment that = XmlDeployment.read(Tccl.get().getResourceAsStream("test/xml/deployment.xml"));
        assertNotNull(that);
        assertEquals(1, that.getJobOperators().size());
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void deploymentAcceptTest() throws Exception {
        final XmlDeployment that = XmlDeployment.read(Tccl.get().getResourceAsStream("test/xml/deployment.xml"));
        that.accept(new XmlDeployment());
        assertEquals(1, that.getJobOperators().size());

        that.accept(new XmlDeployment(), Op.values());
        assertEquals(0, that.getJobOperators().size());

        that.accept(XmlDeployment.read(Tccl.get().getResourceAsStream("test/xml/deployment.xml")), Op.values());
        assertEquals(1, that.getJobOperators().size());
    }

    @Test
    public void jobOperatorTest() throws Exception {
        final XmlJobOperator that = XmlJobOperator.read(Tccl.get().getResourceAsStream("test/xml/job-operator.xml"));
        assertNotNull(that);
        that.write(new ByteArrayOutputStream());
    }

    @Test
    public void jobOperatorAcceptTest() throws Exception {
        final XmlJobOperator that = XmlJobOperator.read(Tccl.get().getResourceAsStream("test/xml/job-operator.xml"));
        that.accept(new XmlJobOperator());

        that.accept(new XmlJobOperator(), Op.values());

        that.accept(XmlJobOperator.read(Tccl.get().getResourceAsStream("test/xml/job-operator.xml")), Op.values());
    }
}
