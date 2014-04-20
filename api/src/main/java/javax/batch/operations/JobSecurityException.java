package javax.batch.operations;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
