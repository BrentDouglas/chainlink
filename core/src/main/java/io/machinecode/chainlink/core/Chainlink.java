package io.machinecode.chainlink.core;

import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.Messages;
import org.jboss.logging.Logger;

import java.util.List;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class Chainlink {

    private static final Logger log = Logger.getLogger(Chainlink.class);

    private static volatile Environment environment;
    private static final Object lock = new Object();

    private Chainlink(){}

    public static void setEnvironment(final Environment environment) {
        synchronized (lock) {
            Chainlink.environment = environment;
            log.debugf("Setting environment to: %s", environment); // TODO Message
            lock.notifyAll();
        }
    }

    public static Environment getEnvironment() throws Exception {
        synchronized (lock) {
            if (Chainlink.environment != null) {
                return Chainlink.environment;
            }
            try {
                final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                final List<Environment> environments = new ResolvableService<>(Constants.ENVIRONMENT, Environment.class)
                        .resolve(tccl);
                if (!environments.isEmpty()) {
                    Chainlink.environment = environments.get(0);
                    return Chainlink.environment;
                }
            } catch (final ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
            }
            while (Chainlink.environment == null) {
                try {
                    log.debugf("Waiting for environment to be set."); // TODO Message
                    lock.wait();
                } catch (final InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return Chainlink.environment;
        }
    }
}
