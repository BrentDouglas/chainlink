package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobExecutionNotRunningException extends BatchRuntimeException {

    public JobExecutionNotRunningException() {
    }

    public JobExecutionNotRunningException(String message) {
        super(message);
    }

    public JobExecutionNotRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobExecutionNotRunningException(Throwable cause) {
        super(cause);
    }
}
