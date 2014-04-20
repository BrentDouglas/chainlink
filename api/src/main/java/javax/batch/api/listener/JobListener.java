package javax.batch.api.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobListener {

    void beforeJob() throws Exception;

    void afterJob() throws Exception;
}
