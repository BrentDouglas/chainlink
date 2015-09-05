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
package io.machinecode.chainlink.core.jsl.fluent;

import io.machinecode.chainlink.core.ExpressionTest;
import io.machinecode.chainlink.core.jsl.impl.JobImpl;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.validation.InvalidJobException;
import io.machinecode.chainlink.core.validation.JobValidator;
import io.machinecode.chainlink.spi.jsl.Job;
import org.junit.Test;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentJobProblemTest {

    @Test(expected = InvalidJobException.class)
    public void multipleIdTest() {
        final Job job = Jsl.job("job1")
                .setRestartable("false")
                .setVersion("1.0")
                .addExecution(Jsl.step("step1")
                        .setNext("step2")
                ).addExecution(Jsl.step("step1")
                );
        final JobImpl impl = JobFactory.produce(job, ExpressionTest.PARAMETERS);
    }

    @Test(expected = InvalidJobException.class)
    public void cycleTest() {
        final Job job = Jsl.job("job1")
                .setRestartable("false")
                .setVersion("1.0")
                .addExecution(Jsl.step("step1")
                        .setNext("step2")
                ).addExecution(Jsl.step("step2")
                        .setNext("step1")
                );
        final JobImpl impl = JobFactory.produce(job, ExpressionTest.PARAMETERS);
    }

    @Test(expected = InvalidJobException.class)
    public void transitionScopeTest() {
        final Job job = Jsl.job("job1")
                .setRestartable("false")
                .setVersion("1.0")
                .addExecution(Jsl.flow("flow1")
                        .setNext("step2")
                        .addExecution(Jsl.flow("flow2")
                                .setNext("flow3")
                        )
                        .addExecution(Jsl.flow("flow3")
                                .setNext("step2") // Fail here
                        )
                ).addExecution(Jsl.step("step2")
                );
        final JobImpl impl = JobFactory.produce(job, ExpressionTest.PARAMETERS);
    }

    @Test(expected = InvalidJobException.class)
    public void invalidTransitionTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addExecution(Jsl.flow("f1")
                        // Need to detect the implicit transition to s1
                        .addExecution(Jsl.step("s1")
                                .setNext("s2")
                                .setTask(Jsl.batchlet("asdf"))
                        ).addExecution(Jsl.step("s2")
                                .setNext("s3") // Fail here
                                .setTask(Jsl.batchlet("asdf"))
                        )
                ).addExecution(Jsl.step("s3")
                        .setTask(Jsl.batchlet("asdf"))
                ), ExpressionTest.PARAMETERS);
    }
}
