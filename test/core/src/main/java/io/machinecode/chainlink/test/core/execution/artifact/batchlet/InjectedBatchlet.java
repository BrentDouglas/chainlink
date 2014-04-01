package io.machinecode.chainlink.test.core.execution.artifact.batchlet;

import io.machinecode.chainlink.spi.util.Reference;
import junit.framework.Assert;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class InjectedBatchlet extends javax.batch.api.AbstractBatchlet {
    public static Reference<Boolean> hasRun = new Reference<Boolean>(false);

    @Inject
    private JobContext jobContext;

    @Inject
    private StepContext stepContext;

    @Inject
    @BatchProperty
    private String property;

    @Inject
    @BatchProperty(name = "property")
    private String otherProperty;

    @Override
    public String process() throws Exception {
        hasRun.set(true);
        Assert.assertNotNull(jobContext);
        Assert.assertNotNull(stepContext);
        Assert.assertEquals("value", property);
        Assert.assertEquals("value", otherProperty);
        return BatchStatus.COMPLETED.toString();
    }
}