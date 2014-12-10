package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
