package io.machinecode.chainlink.core;

import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class Chainlink {

    private static final Logger log = Logger.getLogger(Chainlink.class);

    private static volatile Environment environment;
    private static final Lock lock = new ReentrantLock();
    private static final Condition condition = lock.newCondition();

    private Chainlink(){}

    public static void setEnvironment(final Environment environment) {
        lock.lock();
        try {
            Chainlink.environment = environment;
            log.debugf("Setting environment to: %s", environment); // TODO Message
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public static Environment getEnvironment() {
        lock.lock();
        try {
            if (Chainlink.environment != null) {
                return Chainlink.environment;
            }
            try {
                final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
                final List<Environment> environments = new ResolvableService<Environment>(Constants.ENVIRONMENT, Environment.class)
                        .resolve(tccl);
                if (!environments.isEmpty()) {
                    Chainlink.environment = environments.get(0);
                    return Chainlink.environment;
                }
            } catch (final Exception e) {
                throw new RuntimeException(Messages.get("CHAINLINK-031001.configuration.exception"), e);
            }
            while (Chainlink.environment == null) {
                try {
                    log.debugf("Waiting for environment to be set."); // TODO Message
                    condition.await();
                } catch (InterruptedException e) {
                    //
                }
            }
            return Chainlink.environment;
        } finally {
            lock.unlock();
        }
    }
}
