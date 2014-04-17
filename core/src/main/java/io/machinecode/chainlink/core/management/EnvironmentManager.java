package io.machinecode.chainlink.core.management;

import io.machinecode.chainlink.core.Constants;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.util.Messages;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class EnvironmentManager {

    public static Environment loadEnvironment() {
        final String className = AccessController.doPrivileged(new PrivilegedAction<String>() {
            @Override
            public String run() {
                return System.getProperty(Constants.ENVIRONMENT);
            }
        });
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        if (className != null) {
            try {
                return ((Environment)tccl.loadClass(className).newInstance());
            } catch (final Exception e) {
                throw new RuntimeException(Messages.get("CHAINLINK-031003.environment.exception"), e);
            }
        }
        final List<Environment> environments;
        try {
            environments = new ResolvableService<Environment>(Environment.class).resolve(tccl);
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException(Messages.get("CHAINLINK-031003.environment.exception"), e);
        }
        if (environments.isEmpty()) {
            return new StaticEnvironment();
        } else {
            try {
                return environments.get(0);
            } catch (final Exception e) {
                throw new IllegalStateException(Messages.get("CHAINLINK-031003.environment.exception"), e);
            }
        }
    }
}
