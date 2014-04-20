package javax.batch.runtime;

import javax.batch.operations.JobOperator;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
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
