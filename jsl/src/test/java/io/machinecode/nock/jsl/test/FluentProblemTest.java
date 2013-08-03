package io.machinecode.nock.jsl.test;

import io.machinecode.nock.jsl.api.Job;
import io.machinecode.nock.jsl.fluent.Jsl;
import io.machinecode.nock.jsl.validation.CycleException;
import io.machinecode.nock.jsl.validation.InvalidJobDefinitionException;
import org.junit.Test;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentProblemTest {

    @Test(expected = InvalidJobDefinitionException.class)
    public void multipleIdTest() {
        final Job job = Jsl.job()
                .setId("job1")
                .setRestartable(false)
                .setVersion("1.0")
                .addExecution(Jsl.stepWithBatchletAndPlan()
                        .setId("step1")
                        .setNext("step2")
                ).addExecution(Jsl.stepWithBatchletAndMapper()
                        .setId("step1")
                ).build();
    }

    @Test(expected = CycleException.class)
    public void cycleTest() {
        final Job job = Jsl.job()
                .setId("job1")
                .setRestartable(false)
                .setVersion("1.0")
                .addExecution(Jsl.stepWithBatchletAndPlan()
                        .setId("step1")
                        .setNext("step2")
                ).addExecution(Jsl.stepWithBatchletAndMapper()
                        .setId("step2")
                        .setNext("step1")
                ).build();
    }
}
