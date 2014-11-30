package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JobExecutionIsRunningException extends BatchRuntimeException {

    public JobExecutionIsRunningException() {
    }

    public JobExecutionIsRunningException(String message) {
        super(message);
    }

    public JobExecutionIsRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobExecutionIsRunningException(Throwable cause) {
        super(cause);
    }
}
