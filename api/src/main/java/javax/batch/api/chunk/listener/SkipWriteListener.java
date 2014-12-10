package javax.batch.api.chunk.listener;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface SkipWriteListener {

    void onSkipWriteItem(List<Object> items, Exception exception) throws Exception;
}
