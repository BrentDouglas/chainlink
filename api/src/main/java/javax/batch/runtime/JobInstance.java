package javax.batch.runtime;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobInstance {

    long getInstanceId();

    String getJobName();
}
