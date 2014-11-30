package javax.batch.api.chunk.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class AbstractItemProcessListener implements ItemProcessListener {

    @Override
    public void beforeProcess(Object item) throws Exception {}

    @Override
    public void afterProcess(Object item, Object result) throws Exception {}

    @Override
    public void onProcessError(Object item, Exception exception) throws Exception {}
}
