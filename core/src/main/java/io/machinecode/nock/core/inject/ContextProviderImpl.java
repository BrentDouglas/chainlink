package io.machinecode.nock.core.inject;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.extension.ContextProvider;
import io.machinecode.nock.spi.util.Pair;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.util.Collections;
import java.util.List;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ContextProviderImpl implements ContextProvider {

    final Context context;

    public ContextProviderImpl(final Context context) {
        this.context = context;
    }

    @Override
    public JobContext getJobContext() {
        return context.getJobContext();
    }

    @Override
    public StepContext getStepContext() {
        return context.getStepContext();
    }

    @Override
    public List<? extends Pair<String, String>> getProperties() {
        return context.getProperties(); //TODO
    }
}
