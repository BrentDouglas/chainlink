package javax.batch.operations;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
