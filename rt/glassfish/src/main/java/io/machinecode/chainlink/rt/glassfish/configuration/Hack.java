package io.machinecode.chainlink.rt.glassfish.configuration;

import io.machinecode.chainlink.spi.management.Mutable;
import org.jvnet.hk2.config.DuckTyped;

/**
 * This is only a thing as Glassfish's proxy wiring wont let a config bean
 * override an interface method with @DuckTyped.
 *
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface Hack<T> {

    @DuckTyped
    Mutable<T> hack();

    class Duck {

        public static <T> Mutable<T> hack(final Hack<T> to) {
            final Class<?> clazz = to.getClass();
            for (final Class<?> interfaz : clazz.getInterfaces()) {
                if (interfaz.getSimpleName().startsWith("Glassfish")
                        && interfaz.getPackage().equals(Hack.class.getPackage())) {
                    try {
                        final Object duck = interfaz.getClassLoader()
                                    .loadClass(interfaz.getName() + "$Duck")
                                    .getConstructors()[0]
                                    .newInstance(to);
                        return (Mutable<T>)duck;
                    } catch (final RuntimeException e) {
                        throw e;
                    } catch (final Exception e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
            throw new IllegalStateException();
        }
    }
}
