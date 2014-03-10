package io.machinecode.chainlink.test.core;

import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.jsl.fluent.Jsl;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.element.execution.Step;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExpressionTest {

    public static final Properties PARAMETERS = new Properties();

    @Test
    public void validJobPropertyTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("job-prop", "step3")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("#{jobProperties['job-prop']}")
                ).addExecution(Jsl.step()
                        .setId("step3")
                ), PARAMETERS);
        JobFactory.validate(job);

        final Step step = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step3", step.getNext());
    }

    @Test
    public void validJobPropertyWithValidDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("job-prop", "step3")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("#{jobProperties['job-prop']}?:step2;")
                ).addExecution(Jsl.step()
                        .setId("step3")
                ), PARAMETERS);
        JobFactory.validate(job);

        final Step step = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step3", step.getNext());
    }

    @Test
    public void validJobPropertyWithInvalidDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("job-prop", "step3")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("#{jobProperties['job-prop']}?:step2")
                ).addExecution(Jsl.step()
                        .setId("step3?:step2") //Stop throwing validation exception
                ), PARAMETERS);
        JobFactory.validate(job);

        final Step step = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step3?:step2", step.getNext());
    }

    @Test
    public void validDefaultJobPropertyTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("job-prop", "step4")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("#{jobProperties['not-a-property']}?:step2;")
                ).addExecution(Jsl.step()
                        .setId("step2")
                ), PARAMETERS);
        JobFactory.validate(job);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step2", step1.getNext());
    }

    @Test
    public void invalidDefaultJobPropertyTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("job-prop", "step4")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("#{jobProperties['not-a-property']}?:step2")
                ).addExecution(Jsl.step()
                        .setId("?:step2")
                ), PARAMETERS);
        JobFactory.validate(job);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("?:step2", step1.getNext());
    }

    @Test
    public void multiplePropsTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "step2")
                .addProperty("prop2", "somewhere")
                .addProperty("prop3", "else")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("#{jobProperties['not-a-property']}#{jobProperties['prop1']}?:#{jobProperties['prop2']}#{jobProperties['prop3']};")
                ).addExecution(Jsl.step()
                        .setId("step2")
                ), PARAMETERS);
        JobFactory.validate(job);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step2", step1.getNext());
    }

    @Test
    public void multiplePropsDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "step")
                .addProperty("prop2", "2")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("#{jobProperties['not']}#{jobProperties['not']}?:#{jobProperties['prop1']}#{jobProperties['not']}#{jobProperties['prop2']}#{jobProperties['not']};")
                ).addExecution(Jsl.step()
                        .setId("step2")
                ), PARAMETERS);
        JobFactory.validate(job);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step2", step1.getNext());
    }

    @Test
    public void multiplePropsStringLiteralTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "step")
                .addProperty("prop2", "2")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("#{jobProperties['not']} #{jobProperties['not']}?:#{jobProperties['prop1']}#{jobProperties['not']}#{jobProperties['prop2']}#{jobProperties['not']};")
                ).addExecution(Jsl.step()
                        .setId(" ")
                ), PARAMETERS);
        JobFactory.validate(job);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals(" ", step1.getNext());
    }

    @Test
    public void multiplePropsStringLiteralDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "step")
                .addProperty("prop2", "2")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("#{jobProperties['not']}#{jobProperties['not']}?:#{jobProperties['prop1']}blah#{jobProperties['not']};")
                ).addExecution(Jsl.step()
                        .setId("stepblah")
                ), PARAMETERS);
        JobFactory.validate(job);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("stepblah", step1.getNext());
    }

    @Test
    public void onlyDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "step")
                .addProperty("prop2", "2")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("?:step2;")
                ).addExecution(Jsl.step()
                        .setId("step2")
                ), PARAMETERS);
        JobFactory.validate(job);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step2", step1.getNext());
    }

    @Test
    public void valueAfterDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "st")
                .addProperty("prop2", "ep")
                .addProperty("prop3", "2")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("blah?:default; not invalid apparently")
                ).addExecution(Jsl.step()
                        .setId("blah not invalid apparently")
                ), PARAMETERS);
        JobFactory.validate(job);
    }

    @Test
    public void somethingOutOfTckTest() {
        System.setProperty("file.name.junit", "myfile2");

        final Properties parameters = new Properties();
        parameters.setProperty("myFilename", "testfile1");

        final Job job = JobFactory.produce(Jsl.job()
                .setId("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "st")
                .addProperty("prop2", "ep")
                .addProperty("prop3", "2")
                .addExecution(Jsl.step()
                        .setId("step1")
                        .setNext("#{jobParameters['unresolving.prop']}?:#{systemProperties['file.separator']};#{jobParameters['infile.name']}?:#{systemProperties['file.name.junit']};.txt")
                ).addExecution(Jsl.step()
                        .setId("step2")
                        .setNext("#{systemProperties['file.separator']}test#{systemProperties['file.separator']}#{jobParameters['myFilename']}.txt")
                ).addExecution(Jsl.step()
                        .setId("/myfile2.txt")
                ).addExecution(Jsl.step()
                        .setId("/test/testfile1.txt")
                ) , parameters);
        JobFactory.validate(job);

        System.clearProperty("file.name.junit");

        final Step step1 = (Step)job.getExecutions().get(0);
        final Step step2 = (Step)job.getExecutions().get(1);
        Assert.assertEquals("/myfile2.txt", step1.getNext());
        Assert.assertEquals("/test/testfile1.txt", step2.getNext());
    }
}
