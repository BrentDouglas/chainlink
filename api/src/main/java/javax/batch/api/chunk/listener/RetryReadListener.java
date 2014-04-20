package javax.batch.api.chunk.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface RetryReadListener {

    void onRetryReadException(Exception exception) throws Exception;
}
