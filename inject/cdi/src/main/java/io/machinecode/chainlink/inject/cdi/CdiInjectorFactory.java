package io.machinecode.chainlink.inject.cdi;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.InjectorFactory;
import io.machinecode.chainlink.spi.inject.Injector;

import javax.naming.NamingException;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class CdiInjectorFactory implements InjectorFactory {
    @Override
    public Injector produce(final Dependencies dependencies, final Properties properties) throws NamingException {
        return new CdiInjector();
    }
}
