package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JobStartException extends BatchRuntimeException {

    public JobStartException() {
    }

    public JobStartException(String message) {
        super(message);
    }

    public JobStartException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobStartException(Throwable cause) {
        super(cause);
    }
}
