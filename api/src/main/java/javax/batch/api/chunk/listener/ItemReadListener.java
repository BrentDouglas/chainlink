package javax.batch.api.chunk.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ItemReadListener {

    void beforeRead() throws Exception;

    void afterRead(Object item) throws Exception;

    void onReadError(Exception exception) throws Exception;
}
