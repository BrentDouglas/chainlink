package io.machinecode.chainlink.test.core.jsl.fluent;

import io.machinecode.chainlink.core.element.JobImpl;
import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.validation.InvalidJobException;
import io.machinecode.chainlink.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.test.core.ExpressionTest;
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
        JobFactory.validate(impl);
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
        JobFactory.validate(impl);
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
        JobFactory.validate(impl);
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
        JobFactory.validate(job);
    }
}
