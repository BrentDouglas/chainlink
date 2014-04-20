package javax.batch.api.chunk.listener;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface ItemWriteListener {

    void beforeWrite(List<Object> items) throws Exception;

    void afterWrite(List<Object> items) throws Exception;

    void onWriteError(List<Object> items, Exception exception) throws Exception;
}
