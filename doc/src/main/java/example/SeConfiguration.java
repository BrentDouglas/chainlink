package example;

import io.machinecode.chainlink.core.Chainlink;
import io.machinecode.chainlink.rt.se.SeEnvironment;

import javax.batch.runtime.BatchRuntime;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SeConfiguration {
    public static void main(final String... args) throws Throwable {
        try (final SeEnvironment environment = new SeEnvironment()) {
            Chainlink.setEnvironment(environment);
            // Run code here e.g.
            BatchRuntime.getJobOperator().start("a_job", new Properties());
        } finally {
            Chainlink.setEnvironment(null);
        }
    }
}
