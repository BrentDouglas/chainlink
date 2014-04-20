package javax.batch.operations;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NoSuchJobExecutionException extends BatchRuntimeException {

    public NoSuchJobExecutionException() {
    }

    public NoSuchJobExecutionException(String message) {
        super(message);
    }

    public NoSuchJobExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchJobExecutionException(Throwable cause) {
        super(cause);
    }
}
