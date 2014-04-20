package javax.batch.api.chunk.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface SkipReadListener {

    void onSkipReadItem(Exception exception) throws Exception;
}
