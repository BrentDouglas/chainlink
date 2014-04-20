package javax.batch.operations;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
