package javax.batch.operations;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
