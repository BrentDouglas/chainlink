package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BatchRuntimeException extends RuntimeException {

    public BatchRuntimeException() {
    }

    public BatchRuntimeException(String message) {
        super(message);
    }

    public BatchRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BatchRuntimeException(Throwable cause) {
        super(cause);
    }
}
