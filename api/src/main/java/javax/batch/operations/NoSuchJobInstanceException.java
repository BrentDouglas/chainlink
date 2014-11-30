package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class NoSuchJobInstanceException extends BatchRuntimeException {

    public NoSuchJobInstanceException() {
    }

    public NoSuchJobInstanceException(String message) {
        super(message);
    }

    public NoSuchJobInstanceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchJobInstanceException(Throwable cause) {
        super(cause);
    }
}
