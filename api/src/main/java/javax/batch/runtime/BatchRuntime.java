package javax.batch.runtime;

import javax.batch.operations.JobOperator;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class BatchRuntime {

    public static JobOperator getJobOperator() {
        return AccessController.doPrivileged(new PrivilegedAction<JobOperator>() {
            @Override
            public JobOperator run() {
                Iterator<JobOperator> it = ServiceLoader.load(JobOperator.class).iterator();
                return it.hasNext() ? it.next() : null;
            }
        });
    }
}
