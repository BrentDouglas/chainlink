package javax.batch.api;

import javax.batch.runtime.StepExecution;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Decider {

    String decide(StepExecution[] executions) throws Exception;
}
