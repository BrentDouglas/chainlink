package javax.batch.api.chunk.listener;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class AbstractItemWriteListener implements ItemWriteListener {

    @Override
    public void beforeWrite(List<Object> items) throws Exception {}

    @Override
    public void afterWrite(List<Object> items) throws Exception {}

    @Override
    public void onWriteError(List<Object> items, Exception exception) throws Exception {}
}
