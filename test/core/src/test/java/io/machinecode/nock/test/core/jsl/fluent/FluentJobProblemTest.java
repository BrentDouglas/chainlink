package io.machinecode.nock.test.core.jsl.fluent;

import io.machinecode.nock.core.factory.JobFactory;
import io.machinecode.nock.core.model.JobImpl;
import io.machinecode.nock.jsl.fluent.Jsl;
import io.machinecode.nock.jsl.validation.InvalidJobException;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.test.core.ExpressionTest;
import org.junit.Test;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentJobProblemTest {

    @Test(expected = InvalidJobException.class)
    public void multipleIdTest() {
        final Job job = Jsl.job()
                .setId("job1")
                .setRestartable("false")
                .setVersion("1.0")
                .addExecution(Jsl.stepWithBatchletAndPlan()
                        .setId("step1")
                        .setNext("step2")
                ).addExecution(Jsl.stepWithBatchletAndMapper()
                        .setId("step1")
                );
        final JobImpl impl = JobFactory.INSTANCE.produceExecution(job, ExpressionTest.PARAMETERS);
        JobFactory.INSTANCE.validate(impl);
    }

    @Test(expected = InvalidJobException.class)
    public void cycleTest() {
        final Job job = Jsl.job()
                .setId("job1")
                .setRestartable("false")
                .setVersion("1.0")
                .addExecution(Jsl.stepWithBatchletAndPlan()
                        .setId("step1")
                        .setNext("step2")
                ).addExecution(Jsl.stepWithBatchletAndMapper()
                        .setId("step2")
                        .setNext("step1")
                );
        final JobImpl impl = JobFactory.INSTANCE.produceExecution(job, ExpressionTest.PARAMETERS);
        JobFactory.INSTANCE.validate(impl);
    }

    @Test(expected = InvalidJobException.class)
    public void transitionScopeTest() {
        final Job job = Jsl.job()
                .setId("job1")
                .setRestartable("false")
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
                                .setNext("step2") // Fail here
                        )
                ).addExecution(Jsl.stepWithBatchletAndMapper()
                        .setId("step2")
                );
        final JobImpl impl = JobFactory.INSTANCE.produceExecution(job, ExpressionTest.PARAMETERS);
        JobFactory.INSTANCE.validate(impl);
    }

    @Test(expected = InvalidJobException.class)
    public void invalidTransitionTest() {
        final Job job = JobFactory.INSTANCE.produceExecution(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addExecution(Jsl.flow()
                        .setId("f1")
                        // Need to detect the implicit transition to s1
                        .addExecution(Jsl.stepWithBatchletAndPlan()
                                .setId("s1")
                                .setNext("s2")
                                .setTask(Jsl.batchlet().setRef("asdf"))
                        ).addExecution(Jsl.stepWithBatchletAndPlan()
                                .setId("s2")
                                .setNext("s3") // Fail here
                                .setTask(Jsl.batchlet().setRef("asdf"))
                        )
                ).addExecution(Jsl.stepWithBatchletAndPlan()
                        .setId("s3")
                        .setTask(Jsl.batchlet().setRef("asdf"))
                ), ExpressionTest.PARAMETERS);
        JobFactory.INSTANCE.validate(job);
    }
}
