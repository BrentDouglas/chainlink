package io.machinecode.chainlink.core;

import io.machinecode.chainlink.core.management.StaticEnvironment;
import io.machinecode.chainlink.spi.Constants;
import io.machinecode.chainlink.core.util.ResolvableService;
import io.machinecode.chainlink.spi.inject.DependencyInjectionExtension;
import io.machinecode.chainlink.spi.management.Environment;
import io.machinecode.chainlink.spi.util.Messages;

import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class Chainlink {

    public static Environment environment() {
        final ClassLoader tccl = Thread.currentThread().getContextClassLoader();
        final List<Environment> environments;
        try {
            environments = new ResolvableService<Environment>(Constants.ENVIRONMENT, Environment.class).resolve(tccl);
        } catch (final Exception e) {
            throw new RuntimeException(Messages.get("CHAINLINK-031003.environment.exception"), e);
        }
        final Environment environment;
        if (environments.isEmpty()) {
            environment = new StaticEnvironment();
        } else {
            try {
                environment = environments.get(0);
            } catch (final Exception e) {
                throw new IllegalStateException(Messages.get("CHAINLINK-031003.environment.exception"), e);
            }
        }
        try {
            for (final DependencyInjectionExtension extension : new ResolvableService<DependencyInjectionExtension>(DependencyInjectionExtension.class).resolve(tccl)) {
                extension.register(environment);
            }
        } catch (final Exception e) {
            throw new IllegalStateException(Messages.get("CHAINLINK-031003.environment.exception"), e);
        }
        return environment;
    }
}
