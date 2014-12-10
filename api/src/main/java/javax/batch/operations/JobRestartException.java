package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobRestartException extends BatchRuntimeException {

    public JobRestartException() {
    }

    public JobRestartException(String message) {
        super(message);
    }

    public JobRestartException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobRestartException(Throwable cause) {
        super(cause);
    }
}
