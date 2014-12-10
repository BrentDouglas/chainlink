package javax.batch.api.chunk.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ItemProcessListener {

    void beforeProcess(Object item) throws Exception;

    void afterProcess(Object item, Object result) throws Exception;

    void onProcessError(Object item, Exception exception) throws Exception;
}
