package io.machinecode.nock.core.local;

import io.machinecode.nock.spi.context.Context;
import io.machinecode.nock.spi.extension.ContextProvider;
import io.machinecode.nock.spi.util.Pair;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class LocalContextProvider implements ContextProvider {

    private static final ThreadLocal<Context> context = new ThreadLocal<Context>();

    public static void set(final Context context) {
        LocalContextProvider.context.set(context);
    }

    public static void unset() {
        LocalContextProvider.context.set(null);
    }

    @Override
    public JobContext getJobContext() {
        return context.get().getJobContext();
    }

    @Override
    public StepContext getStepContext() {
        return context.get().getStepContext();
    }

    @Override
    public List<? extends Pair<String, String>> getProperties() {
        return Collections.emptyList(); //TODO
    }
}
