package javax.batch.api.chunk.listener;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface SkipWriteListener {

    void onSkipWriteItem(List<Object> items, Exception exception) throws Exception;
}
