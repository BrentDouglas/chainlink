package javax.batch.operations;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
