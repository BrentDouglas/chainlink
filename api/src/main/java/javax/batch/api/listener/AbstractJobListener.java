package javax.batch.api.listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class AbstractJobListener implements JobListener {

    @Override
    public void beforeJob() throws Exception {}

    @Override
    public void afterJob() throws Exception {}
}
