package javax.batch.operations;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
