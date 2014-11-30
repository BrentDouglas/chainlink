package javax.batch.api.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface StepListener {

    void beforeStep() throws Exception;

    void afterStep() throws Exception;
}
