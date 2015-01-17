package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.factory.ClassLoaderFactory;

import java.lang.ref.WeakReference;
import java.util.Properties;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class ClassLoaderFactoryImpl implements ClassLoaderFactory {

    final WeakReference<ClassLoader> classLoader;

    public ClassLoaderFactoryImpl() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ClassLoaderFactoryImpl(final ClassLoader classLoader) {
        this(new WeakReference<>(classLoader));
    }

    public ClassLoaderFactoryImpl(final WeakReference<ClassLoader> classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public ClassLoader produce(final Dependencies dependencies, final Properties properties) throws Exception {
        return classLoader.get();
    }
}
