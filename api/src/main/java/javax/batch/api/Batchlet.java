package javax.batch.api;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface Batchlet {

    String process() throws Exception;

    void stop() throws Exception;
}
