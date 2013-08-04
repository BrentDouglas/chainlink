package io.machinecode.nock.jsl.test;

import io.machinecode.nock.jsl.api.Job;
import io.machinecode.nock.jsl.fluent.Jsl;
import io.machinecode.nock.jsl.validation.InvalidJobException;
import io.machinecode.nock.jsl.validation.InvalidTransitionException;
import org.junit.Test;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentProblemTest {

    @Test(expected = InvalidJobException.class)
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

    @Test(expected = InvalidTransitionException.class)
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

    @Test(expected = InvalidTransitionException.class)
    public void transitionScopeTest() {
        final Job job = Jsl.job()
                .setId("job1")
                .setRestartable(false)
                .setVersion("1.0")
                .addExecution(Jsl.flow()
                        .setId("flow1")
                        .setNext("step2")
                        .addExecution(Jsl.flow()
                                .setId("flow2")
                                .setNext("flow3")
                        )
                        .addExecution(Jsl.flow()
                                .setId("flow3")
                                .setNext("step2") //Should throw
                        )
                ).addExecution(Jsl.stepWithBatchletAndMapper()
                        .setId("step2")
                ).build();
    }
}
