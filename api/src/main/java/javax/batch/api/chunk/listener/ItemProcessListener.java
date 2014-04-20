package javax.batch.api.chunk.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ItemProcessListener {

    void beforeProcess(Object item) throws Exception;

    void afterProcess(Object item, Object result) throws Exception;

    void onProcessError(Object item, Exception exception) throws Exception;
}
