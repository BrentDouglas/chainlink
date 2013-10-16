package io.machinecode.nock.spi.extension;

import io.machinecode.nock.spi.util.Pair;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ContextProvider {

    JobContext getJobContext();

    StepContext getStepContext();

    List<? extends Pair<String, String>> getProperties();
}
