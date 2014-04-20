package javax.batch.api.chunk.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class AbstractItemProcessListener implements ItemProcessListener {

    @Override
    public void beforeProcess(Object item) throws Exception {}

    @Override
    public void afterProcess(Object item, Object result) throws Exception {}

    @Override
    public void onProcessError(Object item, Exception exception) throws Exception {}
}
