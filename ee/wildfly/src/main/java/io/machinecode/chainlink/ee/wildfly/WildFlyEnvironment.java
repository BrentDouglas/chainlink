package io.machinecode.chainlink.ee.wildfly;

import io.machinecode.chainlink.core.management.JobOperatorImpl;
import io.machinecode.chainlink.spi.exception.NoConfigurationWithIdException;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.management.ExtendedJobOperator;
import org.jboss.msc.service.ServiceName;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * TODO
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class WildFlyEnvironment implements Environment {

    private final ConcurrentMap<String, App> operators = new ConcurrentHashMap<String, App>();

    @Override
    public ExtendedJobOperator getJobOperator(final String name) throws NoConfigurationWithIdException {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                final JobOperatorImpl op = app.ops.get(name);
                if (op == null) {
                    throw new NoConfigurationWithIdException("No configuration for id: " + name); //TODO Message
                }
                return op;
            }
        }
        throw new NoConfigurationWithIdException("Chainlink not configured for TCCL: " + tccl); //TODO Message
    }

    @Override
    public Map<String, JobOperatorImpl> getJobOperators() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        for (final App app : operators.values()) {
            if (tccl.equals(app.loader.get())) {
                return app.ops;
            }
        }
        return Collections.emptyMap();
    }

    public void addOperator(final ServiceName module, final String name, final ClassLoader loader, final JobOperatorImpl operator) throws Exception {
        final String cn = module.getCanonicalName();
        App app = this.operators.get(cn);
        if (app == null) {
            app = new App(loader);
            this.operators.put(cn, app);
        }
        app.ops.put(name, operator);
    }

    public void removeOperator(final ServiceName module, final String name) throws Exception {
        final String cn = module.getCanonicalName();
        final App app = this.operators.get(cn);
        Exception exception = null;
        for (final Map.Entry<String, JobOperatorImpl> entry : app.ops.entrySet()) {
            if (name.equals(entry.getKey())) {
                try {
                    entry.getValue().close();
                } catch (Exception e) {
                    if (exception == null) {
                        exception = e;
                    } else {
                        exception.addSuppressed(e);
                    }
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    private static class App {
        final WeakReference<ClassLoader> loader;
        final ConcurrentMap<String, JobOperatorImpl> ops;

        private App(final ClassLoader loader) {
            this.loader = new WeakReference<ClassLoader>(loader);
            this.ops = new ConcurrentHashMap<String, JobOperatorImpl>();
        }
    }
}
