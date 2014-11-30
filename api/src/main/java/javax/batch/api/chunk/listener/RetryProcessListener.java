package javax.batch.api.chunk.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface RetryProcessListener {

    void onRetryProcessException(Object item, Exception exception) throws Exception;
}
