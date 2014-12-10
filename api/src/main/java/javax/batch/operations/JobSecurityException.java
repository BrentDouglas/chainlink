package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class JobSecurityException extends BatchRuntimeException {

    public JobSecurityException() {
    }

    public JobSecurityException(String message) {
        super(message);
    }

    public JobSecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobSecurityException(Throwable cause) {
        super(cause);
    }
}
