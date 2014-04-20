package javax.batch.operations;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
