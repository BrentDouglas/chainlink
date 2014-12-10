package javax.batch.api.chunk.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface ItemReadListener {

    void beforeRead() throws Exception;

    void afterRead(Object item) throws Exception;

    void onReadError(Exception exception) throws Exception;
}
