package javax.batch.api.chunk.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface RetryReadListener {

    void onRetryReadException(Exception exception) throws Exception;
}
