package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
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
