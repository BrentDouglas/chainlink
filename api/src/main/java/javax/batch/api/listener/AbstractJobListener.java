package javax.batch.api.listener;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class AbstractJobListener implements JobListener {

    @Override
    public void beforeJob() throws Exception {}

    @Override
    public void afterJob() throws Exception {}
}
