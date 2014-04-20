package javax.batch.api.chunk.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface RetryProcessListener {

    void onRetryProcessException(Object item, Exception exception) throws Exception;
}
