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
package io.machinecode.chainlink.test.cdi;

import io.machinecode.chainlink.inject.cdi.CdiArtifactLoader;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.core.execution.chunk.CascadingFailChunkTest;
import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CdiCascadingFailChunkTest extends CascadingFailChunkTest {

    private static Weld weld;
    private static WeldContainer container;

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        super.visitJobOperatorModel(model);
        model.getArtifactLoaders().add().setValue(new CdiArtifactLoader(container.getBeanManager()));
    }

    @BeforeClass
    public static void beforeClass() {
        weld = new Weld();
        container = weld.initialize();
    }

    @AfterClass
    public static void afterClass() {
        weld.shutdown();
    }
}
