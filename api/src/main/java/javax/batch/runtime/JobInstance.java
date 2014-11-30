package javax.batch.runtime;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public interface JobInstance {

    long getInstanceId();

    String getJobName();
}
