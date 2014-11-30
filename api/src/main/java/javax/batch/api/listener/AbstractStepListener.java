package javax.batch.api.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class AbstractStepListener implements StepListener {

    @Override
    public void beforeStep() throws Exception {}

    @Override
    public void afterStep() throws Exception {}
}
