package io.machinecode.chainlink.core.execution.artifact.batchlet;

import io.machinecode.chainlink.core.base.Reference;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
*/
public class OverrideBatchlet extends javax.batch.api.AbstractBatchlet {

    public static final Reference<Boolean> hasRun = new Reference<>(false);

    @Inject
    private StepContext stepContext;

    @Inject
    private JobContext jobContext;

    @Override
    public String process() throws Exception {
        hasRun.set(true);
        stepContext.setExitStatus("Step Exit Status");
        jobContext.setExitStatus("Job Exit Status");
        return BatchStatus.COMPLETED.toString();
    }

    public static void reset() {
        hasRun.set(false);
    }
}
