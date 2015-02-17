package io.machinecode.chainlink.coherence.test.transport;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CoherenceTestRunner extends BlockJUnit4ClassRunner {

    public static ClassLoader loader;

    public CoherenceTestRunner(final Class<?> clazz) throws InitializationError {
        super(reload(clazz));
        loader = getTestClass().getJavaClass().getClassLoader();
    }

    private static Class<?> reload(final Class<?> clazz) throws InitializationError {
        final CoherenceClassLoader loader = new CoherenceClassLoader(clazz.getClassLoader());
        try {
            return loader.loadClass(clazz.getName());
        } catch (ClassNotFoundException e) {
            throw new InitializationError(e);
        }
    }

    @Override
    public void run(final RunNotifier notifier) {
        final Thread ct = Thread.currentThread();
        final ClassLoader tccl = ct.getContextClassLoader();
        ct.setContextClassLoader(loader);
        try {
            super.run(notifier);
        } finally {
            ct.setContextClassLoader(tccl);
        }
    }
}
