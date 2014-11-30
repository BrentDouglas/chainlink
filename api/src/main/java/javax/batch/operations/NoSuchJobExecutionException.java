package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
