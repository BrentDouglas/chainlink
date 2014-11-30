package javax.batch.api.chunk.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface SkipReadListener {

    void onSkipReadItem(Exception exception) throws Exception;
}
