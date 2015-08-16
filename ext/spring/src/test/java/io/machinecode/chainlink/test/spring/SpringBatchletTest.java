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
package io.machinecode.chainlink.test.spring;

import io.machinecode.chainlink.inject.spring.SpringArtifactLoader;
import io.machinecode.chainlink.spi.configuration.JobOperatorModel;
import io.machinecode.chainlink.core.execution.batchlet.BatchletTest;
import org.junit.BeforeClass;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SpringBatchletTest extends BatchletTest {

    private static AbstractApplicationContext context;

    @Override
    protected void visitJobOperatorModel(final JobOperatorModel model) throws Exception {
        model.getArtifactLoaders().add().setValue(context.getBean(SpringArtifactLoader.class));
    }

    @BeforeClass
    public static void beforeClass() {
        context = new ClassPathXmlApplicationContext("beans.xml");
    }
}
