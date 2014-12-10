package javax.batch.api;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Batchlet {

    String process() throws Exception;

    void stop() throws Exception;
}
