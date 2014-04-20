package javax.batch.api;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface Batchlet {

    String process() throws Exception;

    void stop() throws Exception;
}
