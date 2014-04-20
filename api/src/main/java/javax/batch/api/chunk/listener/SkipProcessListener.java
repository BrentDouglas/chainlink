package javax.batch.api.chunk.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface SkipProcessListener {

    void onSkipProcessItem(Object item, Exception exception) throws Exception;
}
