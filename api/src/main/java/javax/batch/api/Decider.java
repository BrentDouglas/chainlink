package javax.batch.api;

import javax.batch.runtime.StepExecution;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Decider {

    String decide(StepExecution[] executions) throws Exception;
}
