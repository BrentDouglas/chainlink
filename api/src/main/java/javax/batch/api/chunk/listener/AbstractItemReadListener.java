package javax.batch.api.chunk.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class AbstractItemReadListener implements ItemReadListener {

    @Override
    public void beforeRead() throws Exception {}

    @Override
    public void afterRead(Object item) throws Exception {}

    @Override
    public void onReadError(Exception exception) throws Exception {}
}
