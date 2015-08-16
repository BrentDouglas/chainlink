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

import io.machinecode.chainlink.core.schema.xml.subsystem.XmlChainlinkSubSystem;
import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.spi.exception.ConfigurationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ModelTest extends Assert {

    @Test
    public void testSubSystem() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final SubSystemModelImpl model = new SubSystemModelImpl(tccl);
        model.loadChainlinkSubsystemXml(tccl.getResourceAsStream("test/model/subsystem.xml"));
        //TODO
    }

    @Test
    public void testChainlink() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final DeploymentModelImpl model = new SubSystemModelImpl(tccl).getDeployment("default");
        model.loadChainlinkXml(tccl.getResourceAsStream("test/configuration/chainlink.xml"));
        //TODO
    }

    @Test(expected = ConfigurationException.class)
    public void testNoSubsystemRef() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final SubSystemModelImpl model = new SubSystemModelImpl(tccl);
        final XmlChainlinkSubSystem subSystem = XmlChainlinkSubSystem.read(tccl.getResourceAsStream("test/model/no-subsystem-ref.xml"));
        Model.configureSubSystem(model, subSystem, tccl);
    }

    @Test(expected = ConfigurationException.class)
    public void testNoDeploymentRef() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final SubSystemModelImpl model = new SubSystemModelImpl(tccl);
        final XmlChainlinkSubSystem subSystem = XmlChainlinkSubSystem.read(tccl.getResourceAsStream("test/model/no-deployment-ref.xml"));
        Model.configureSubSystem(model, subSystem, tccl);
    }

    @Test(expected = ConfigurationException.class)
    public void testNoJobOperatorRef() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final SubSystemModelImpl model = new SubSystemModelImpl(tccl);
        final XmlChainlinkSubSystem subSystem = XmlChainlinkSubSystem.read(tccl.getResourceAsStream("test/model/no-job-operator-ref.xml"));
        Model.configureSubSystem(model, subSystem, tccl);
    }

    @Test(expected = ConfigurationException.class)
    public void testWrongTypeSubsystemRef() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final SubSystemModelImpl model = new SubSystemModelImpl(tccl);
        final XmlChainlinkSubSystem subSystem = XmlChainlinkSubSystem.read(tccl.getResourceAsStream("test/model/wrong-type-subsystem-ref.xml"));
        Model.configureSubSystem(model, subSystem, tccl);
    }

    @Test(expected = ConfigurationException.class)
    public void testWrongTypeDeploymentRef() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final SubSystemModelImpl model = new SubSystemModelImpl(tccl);
        final XmlChainlinkSubSystem subSystem = XmlChainlinkSubSystem.read(tccl.getResourceAsStream("test/model/wrong-type-deployment-ref.xml"));
        Model.configureSubSystem(model, subSystem, tccl);
    }

    @Test(expected = ConfigurationException.class)
    public void testWrongTypeJobOperatorRef() throws Exception {
        final ClassLoader tccl = Tccl.get();
        final SubSystemModelImpl model = new SubSystemModelImpl(tccl);
        final XmlChainlinkSubSystem subSystem = XmlChainlinkSubSystem.read(tccl.getResourceAsStream("test/model/wrong-type-job-operator-ref.xml"));
        Model.configureSubSystem(model, subSystem, tccl);
    }
}
