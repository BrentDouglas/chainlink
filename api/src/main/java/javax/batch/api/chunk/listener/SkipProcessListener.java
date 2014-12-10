package javax.batch.api.chunk.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface SkipProcessListener {

    void onSkipProcessItem(Object item, Exception exception) throws Exception;
}
