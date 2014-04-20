package javax.batch.api.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class AbstractStepListener implements StepListener {

    @Override
    public void beforeStep() throws Exception {}

    @Override
    public void afterStep() throws Exception {}
}
