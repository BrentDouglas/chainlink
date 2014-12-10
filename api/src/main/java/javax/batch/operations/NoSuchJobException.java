package javax.batch.operations;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NoSuchJobException extends BatchRuntimeException {

    public NoSuchJobException() {
    }

    public NoSuchJobException(String message) {
        super(message);
    }

    public NoSuchJobException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchJobException(Throwable cause) {
        super(cause);
    }
}
