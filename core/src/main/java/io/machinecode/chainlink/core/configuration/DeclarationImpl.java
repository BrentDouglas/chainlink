package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.Declaration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.configuration.Resource;
import io.machinecode.chainlink.spi.configuration.factory.Factory;
import io.machinecode.chainlink.spi.exception.UnresolvableResourceException;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DeclarationImpl<T> implements Declaration<T>, Resource<T> {

    final WeakReference<ClassLoader> loader;
    final Set<String> names;
    final Map<String, DeclarationImpl<?>> owner;
    final Class<?> clazz;
    String name;
    Properties properties;
    T value;
    Factory<? extends T> defaultFactory;
    Factory<? extends T> factory;
    Class<? extends Factory<? extends T>> factoryClass;
    String fqcn;

    T cache;

    public DeclarationImpl(final WeakReference<ClassLoader> loader, final Set<String> names, final Map<String, DeclarationImpl<?>> owner,
                           final Class<?> clazz) {
        this.loader = loader;
        this.names = names;
        this.owner = owner;
        this.clazz = clazz;
    }

    public DeclarationImpl(final WeakReference<ClassLoader> loader, final Set<String> names, final Map<String, DeclarationImpl<?>> owner,
                           final Class<?> clazz, final String name) {
        this(loader, names, owner, clazz);
        setName(name);
    }

    private DeclarationImpl(final DeclarationImpl<T> that, final Set<String> names, final Map<String, DeclarationImpl<?>> owner) {
        this(that.loader, names, owner, that.clazz);
        this.name = that.name;
        this.properties = new Properties(that.properties);
        this.value = that.value;
        this.defaultFactory = that.defaultFactory;
        this.factory = that.factory;
        this.factoryClass = that.factoryClass;
        this.fqcn = that.fqcn;
        this.cache = that.cache;
    }

    public DeclarationImpl<T> copy(final Set<String> names, final Map<String, DeclarationImpl<?>> owner) {
        return new DeclarationImpl<>(this, names, owner);
    }

    boolean is(final Class<?> type) {
        return clazz.isAssignableFrom(type);
    }

    @Override
    public DeclarationImpl<T> setProperties(final Properties properties) {
        this.properties = properties;
        return this;
    }

    @Override
    public DeclarationImpl<T> set(final T that) {
        this.value = that;
        return this;
    }

    public DeclarationImpl<T> setName(final String name) {
        if (name == null) {
            throw new IllegalStateException(); //TODO Message, should only really be called from xml for XmlJobOperator
        }
        if (name.equals(this.name)) {
            return this;
        }
        final String old = this.name;
        this.name = name;
        if (!names.add(name)) {
            throw new IllegalStateException("Resource already declared for name: " + name); //TODO Message and better exception
        } else {
            names.remove(old);
        }
        if (owner.containsValue(this)) {
            return this;
        }
        owner.put(name, this);
        return this;
    }

    @Override
    public DeclarationImpl<T> setDefaultValueFactory(final Factory<? extends T> that) {
        this.cache = null;
        this.defaultFactory = that;
        return this;
    }

    @Override
    public DeclarationImpl<T> setFactory(final Factory<? extends T> that) {
        this.cache = null;
        this.factory = that;
        return this;
    }

    @Override
    public DeclarationImpl<T> setFactoryClass(final Class<? extends Factory<? extends T>> that) {
        this.cache = null;
        this.factoryClass = that;
        return this;
    }

    @Override
    public DeclarationImpl<T> setFactoryFqcn(final String fqcn) {
        this.cache = null;
        this.fqcn = fqcn;
        return this;
    }

    @Override
    public T get(final Dependencies dependencies) {
        if (cache != null) {
            return cache;
        }
        if (value != null) {
            return cache = value;
        }
        final Properties properties = this.properties == null
                ? new Properties()
                : this.properties;
        try {
            if (factory != null) {
                return cache = factory.produce(dependencies, properties);
            } else if (factoryClass != null) {
                return cache = factoryClass.newInstance().produce(dependencies, properties);
            } else if (fqcn != null) {
                final ClassLoader loader = this.loader.get();
                if (loader == null) {
                    throw new UnresolvableResourceException(); //TODO Message
                }
                @SuppressWarnings("unchecked")
                final Class<? extends Factory<? extends T>> clazz = (Class<? extends Factory<? extends T>>)loader.loadClass(fqcn);
                return cache = clazz.newInstance().produce(dependencies, properties);
            } else if (defaultFactory != null) {
                return cache = defaultFactory.produce(dependencies, properties);
            }
        } catch (final UnresolvableResourceException e) {
            throw e;
        } catch (final Exception e) {
            throw new UnresolvableResourceException(e); //TODO Message
        }
        throw new UnresolvableResourceException("No configuration found for node with name=" + name + ",clazz=" + clazz.getSimpleName()); //TODO
    }
}
