package io.machinecode.nock.core.local;

import io.machinecode.nock.spi.inject.Injectables;
import io.machinecode.nock.spi.util.Pair;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InjectablesImpl implements Injectables {

    private final JobContext jobContext;
    private final StepContext stepContext;
    private final List<? extends Pair<String, String>> properties;

    public InjectablesImpl(final JobContext jobContext, final StepContext stepContext, final List<? extends Pair<String, String>> properties) {
        this.jobContext = jobContext;
        this.stepContext = stepContext;
        this.properties = properties;
    }

    @Override
    public JobContext getJobContext() {
        return jobContext;
    }

    @Override
    public StepContext getStepContext() {
        return stepContext;
    }

    @Override
    public List<? extends Pair<String, String>> getProperties() {
        return properties;
    }
}
