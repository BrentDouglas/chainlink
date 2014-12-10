package javax.batch.api.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface JobListener {

    void beforeJob() throws Exception;

    void afterJob() throws Exception;
}
