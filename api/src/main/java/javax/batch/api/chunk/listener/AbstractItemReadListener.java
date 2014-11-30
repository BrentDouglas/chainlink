package javax.batch.api.chunk.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class AbstractItemReadListener implements ItemReadListener {

    @Override
    public void beforeRead() throws Exception {}

    @Override
    public void afterRead(Object item) throws Exception {}

    @Override
    public void onReadError(Exception exception) throws Exception {}
}
