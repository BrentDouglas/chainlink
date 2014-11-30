package io.machinecode.chainlink.spi.inject;

import io.machinecode.chainlink.spi.util.Pair;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Injectables {

    JobContext getJobContext();

    StepContext getStepContext();

    List<? extends Pair<String, String>> getProperties();
}
