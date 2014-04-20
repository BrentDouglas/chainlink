package javax.batch.operations;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
