package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JobExecutionAlreadyCompleteException extends BatchRuntimeException {

    public JobExecutionAlreadyCompleteException() {
    }

    public JobExecutionAlreadyCompleteException(String message) {
        super(message);
    }

    public JobExecutionAlreadyCompleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobExecutionAlreadyCompleteException(Throwable cause) {
        super(cause);
    }
}
