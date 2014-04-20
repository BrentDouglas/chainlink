package javax.batch.api.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface StepListener {

    void beforeStep() throws Exception;

    void afterStep() throws Exception;
}
