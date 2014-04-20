package javax.batch.operations;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobExecutionNotMostRecentException extends BatchRuntimeException {

    public JobExecutionNotMostRecentException() {
    }

    public JobExecutionNotMostRecentException(String message) {
        super(message);
    }

    public JobExecutionNotMostRecentException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobExecutionNotMostRecentException(Throwable cause) {
        super(cause);
    }
}
