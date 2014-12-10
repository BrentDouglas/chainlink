package javax.batch.api.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public abstract class AbstractStepListener implements StepListener {

    @Override
    public void beforeStep() throws Exception {}

    @Override
    public void afterStep() throws Exception {}
}
