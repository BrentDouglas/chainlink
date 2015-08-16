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
package io.machinecode.chainlink.core;

import io.machinecode.chainlink.core.factory.JobFactory;
import io.machinecode.chainlink.core.jsl.fluent.Jsl;
import io.machinecode.chainlink.core.property.ArrayPropertyLookup;
import io.machinecode.chainlink.core.property.SinglePropertyLookup;
import io.machinecode.chainlink.core.property.SystemPropertyLookup;
import io.machinecode.chainlink.core.validation.JobValidator;
import io.machinecode.chainlink.spi.jsl.Job;
import io.machinecode.chainlink.spi.jsl.execution.Step;
import junit.framework.Assert;
import org.junit.Test;

import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ExpressionTest {

    public static final Properties PARAMETERS = new Properties();

    @Test
    public void validJobPropertyTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("job-prop", "step3")
                .addExecution(Jsl.step("step1")
                        .setNext("#{jobProperties['job-prop']}")
                ).addExecution(Jsl.step("step3")
                ), PARAMETERS);
        JobValidator.validate(job);

        final Step step = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step3", step.getNext());
    }

    @Test
    public void validJobPropertyWithValidDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("job-prop", "step3")
                .addExecution(Jsl.step("step1")
                        .setNext("#{jobProperties['job-prop']}?:step2;")
                ).addExecution(Jsl.step("step3")
                ), PARAMETERS);
        JobValidator.validate(job);

        final Step step = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step3", step.getNext());
    }

    @Test
    public void validJobPropertyWithInvalidDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("job-prop", "step3")
                .addExecution(Jsl.step("step1")
                        .setNext("#{jobProperties['job-prop']}?:step2")
                ).addExecution(Jsl.step("step3?:step2") //Stop throwing validation exception
                ), PARAMETERS);
        JobValidator.validate(job);

        final Step step = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step3?:step2", step.getNext());
    }

    @Test
    public void validDefaultJobPropertyTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("job-prop", "step4")
                .addExecution(Jsl.step("step1")
                        .setNext("#{jobProperties['not-a-property']}?:step2;")
                ).addExecution(Jsl.step("step2")
                ), PARAMETERS);
        JobValidator.validate(job);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step2", step1.getNext());
    }

    @Test
    public void invalidDefaultJobPropertyTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("job-prop", "step4")
                .addExecution(Jsl.step("step1")
                        .setNext("#{jobProperties['not-a-property']}?:step2")
                ).addExecution(Jsl.step("?:step2")
                ), PARAMETERS);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("?:step2", step1.getNext());
    }

    @Test
    public void multiplePropsTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "step2")
                .addProperty("prop2", "somewhere")
                .addProperty("prop3", "else")
                .addExecution(Jsl.step("step1")
                        .setNext("#{jobProperties['not-a-property']}#{jobProperties['prop1']}?:#{jobProperties['prop2']}#{jobProperties['prop3']};")
                ).addExecution(Jsl.step("step2")
                ), PARAMETERS);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step2", step1.getNext());
    }

    @Test
    public void multiplePropsDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "step")
                .addProperty("prop2", "2")
                .addExecution(Jsl.step("step1")
                        .setNext("#{jobProperties['not']}#{jobProperties['not']}?:#{jobProperties['prop1']}#{jobProperties['not']}#{jobProperties['prop2']}#{jobProperties['not']};")
                ).addExecution(Jsl.step("step2")
                ), PARAMETERS);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step2", step1.getNext());
    }

    @Test
    public void multiplePropsStringLiteralTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "step")
                .addProperty("prop2", "2")
                .addExecution(Jsl.step("step1")
                        .setNext("#{jobProperties['not']} #{jobProperties['not']}?:#{jobProperties['prop1']}#{jobProperties['not']}#{jobProperties['prop2']}#{jobProperties['not']};")
                ).addExecution(Jsl.step(" ")
                ), PARAMETERS);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals(" ", step1.getNext());
    }

    @Test
    public void multiplePropsStringLiteralDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "step")
                .addProperty("prop2", "2")
                .addExecution(Jsl.step("step1")
                        .setNext("#{jobProperties['not']}#{jobProperties['not']}?:#{jobProperties['prop1']}blah#{jobProperties['not']};")
                ).addExecution(Jsl.step("stepblah")
                ), PARAMETERS);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("stepblah", step1.getNext());
    }

    @Test
    public void onlyDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "step")
                .addProperty("prop2", "2")
                .addExecution(Jsl.step("step1")
                        .setNext("?:step2;")
                ).addExecution(Jsl.step("step2")
                ), PARAMETERS);

        final Step step1 = (Step)job.getExecutions().get(0);
        Assert.assertEquals("step2", step1.getNext());
    }

    @Test
    public void valueAfterDefaultTest() {
        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "st")
                .addProperty("prop2", "ep")
                .addProperty("prop3", "2")
                .addExecution(Jsl.step("step1")
                        .setNext("blah?:default; not invalid apparently")
                ).addExecution(Jsl.step("blah not invalid apparently")
                ), PARAMETERS);
    }

    @Test
    public void somethingOutOfTckTest() {
        System.setProperty("file.name.junit", "myfile2");

        final Properties parameters = new Properties();
        parameters.setProperty("myFilename", "testfile1");

        final Job job = JobFactory.produce(Jsl.job("i1")
                .setRestartable("false")
                .setVersion("1.0")
                .addProperty("prop1", "st")
                .addProperty("prop2", "ep")
                .addProperty("prop3", "2")
                .addExecution(Jsl.step("step1")
                        .setNext("#{jobParameters['unresolving.prop']}?:#{systemProperties['file.separator']};#{jobParameters['infile.name']}?:#{systemProperties['file.name.junit']};.txt")
                ).addExecution(Jsl.step("step2")
                        .setNext("#{systemProperties['file.separator']}test#{systemProperties['file.separator']}#{jobParameters['myFilename']}.txt")
                ).addExecution(Jsl.step("/myfile2.txt")
                ).addExecution(Jsl.step("/test/testfile1.txt")
                ) , parameters);

        System.clearProperty("file.name.junit");

        final Step step1 = (Step)job.getExecutions().get(0);
        final Step step2 = (Step)job.getExecutions().get(1);
        Assert.assertEquals("/myfile2.txt", step1.getNext());
        Assert.assertEquals("/test/testfile1.txt", step2.getNext());
    }

    @Test
    public void withConfigPropertyTest() {
        final Properties config = new Properties();
        config.setProperty("foo", "config");
        config.setProperty("bar", "config");
        System.setProperty("foo", "system");
        try {
            final Job job = JobFactory.produce(Jsl.job("i1")
                    .setRestartable("false")
                    .setVersion("1.0")
                    .addProperty("foo", "local")
                    .addExecution(Jsl.step("#{systemProperties['foo']}#{systemProperties['bar']}")
                    ), PARAMETERS, new SystemPropertyLookup(new SinglePropertyLookup(config)));

            final Step step1 = (Step)job.getExecutions().get(0);
            Assert.assertEquals("systemconfig", step1.getId());
        } finally {
            System.clearProperty("foo");
        }
    }

    @Test
    public void withMultipleConfigPropertyTest() {
        final Properties parent = new Properties();
        parent.setProperty("foo", "parent");
        parent.setProperty("bar", "parent");
        parent.setProperty("baz", "parent");

        final Properties config = new Properties();
        config.setProperty("foo", "config");
        config.setProperty("bar", "config");

        System.setProperty("foo", "system");
        try {
            final Job job = JobFactory.produce(Jsl.job("i1")
                    .setRestartable("false")
                    .setVersion("1.0")
                    .addProperty("foo", "local")
                    .addExecution(Jsl.step("#{systemProperties['foo']}#{systemProperties['bar']}#{systemProperties['baz']}")
                    ), PARAMETERS, new SystemPropertyLookup(new ArrayPropertyLookup(config, parent)));

            final Step step1 = (Step)job.getExecutions().get(0);
            Assert.assertEquals("systemconfigparent", step1.getId());
        } finally {
            System.clearProperty("foo");
        }
    }
}
