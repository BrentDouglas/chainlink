package io.machinecode.chainlink.core.configuration;

import io.machinecode.chainlink.spi.configuration.ConfigurationLoader;
import io.machinecode.chainlink.spi.configuration.Declaration;
import io.machinecode.chainlink.spi.configuration.Dependencies;
import io.machinecode.chainlink.spi.property.PropertyLookup;
import io.machinecode.chainlink.spi.configuration.factory.Factory;
import io.machinecode.chainlink.spi.exception.UnresolvableResourceException;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class DeclarationImpl<T> implements Declaration<T> {

    final WeakReference<ClassLoader> loader;
    final Set<String> names;
    final Map<String, DeclarationImpl<?>> owner;
    final Class<? extends T> valueInterface;
    final Class<? extends Factory<? extends T>> factoryInterface;
    String name;
    T value;
    Factory<? extends T> defaultFactory;
    Factory<? extends T> factory;
    Class<? extends T> valueClass;
    Class<? extends Factory<? extends T>> factoryClass;
    String ref;

    T cache;

    public DeclarationImpl(final WeakReference<ClassLoader> loader, final Set<String> names, final Map<String, DeclarationImpl<?>> owner,
                           final Class<? extends T> valueInterface, final Class<? extends Factory<? extends T>> factoryInterface) {
        this.loader = loader;
        this.names = names;
        this.owner = owner;
        this.valueInterface = valueInterface;
        this.factoryInterface = factoryInterface;
    }

    public DeclarationImpl(final WeakReference<ClassLoader> loader, final Set<String> names, final Map<String, DeclarationImpl<?>> owner,
                           final Class<? extends T> valueInterface, final Class<? extends Factory<? extends T>> factoryInterface, final String name) {
        this(loader, names, owner, valueInterface, factoryInterface);
        setName(name);
    }

    private DeclarationImpl(final DeclarationImpl<T> that, final Set<String> names, final Map<String, DeclarationImpl<?>> owner) {
        this(that.loader, names, owner, that.valueInterface, that.factoryInterface);
        this.name = that.name;
        this.value = that.value;
        this.defaultFactory = that.defaultFactory;
        this.factory = that.factory;
        this.factoryClass = that.factoryClass;
        this.ref = that.ref;
        this.cache = that.cache;
    }

    public DeclarationImpl<T> copy(final Set<String> names, final Map<String, DeclarationImpl<?>> owner) {
        return new DeclarationImpl<>(this, names, owner);
    }

    boolean is(final Class<?> type) {
        return valueInterface.isAssignableFrom(type);
    }

    @Override
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
    public DeclarationImpl<T> setValue(final T that) {
        this.cache = null;
        this.value = that;
        return this;
    }

    @Override
    public Declaration<T> setValueClass(final Class<? extends T> that) {
        this.cache = null;
        this.valueClass = that;
        return this;
    }

    @Override
    public DeclarationImpl<T> setDefaultFactory(final Factory<? extends T> that) {
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
    public DeclarationImpl<T> setRef(final String that) {
        this.cache = null;
        this.ref = that;
        return this;
    }

    public T get(final Dependencies dependencies, final PropertyLookup properties, final ConfigurationLoader configurationLoader) {
        if (cache != null) {
            return cache;
        }
        if (value != null) {
            return cache = value;
        }
        try {
            if (valueClass != null) {
                return cache = valueClass.newInstance();
            } else if (factory != null) {
                return cache = factory.produce(dependencies, properties);
            } else if (factoryClass != null) {
                return cache = factoryClass.newInstance().produce(dependencies, properties);
            } else if (ref != null) {
                final ClassLoader classLoader = this.loader.get();
                if (classLoader == null) {
                    throw new UnresolvableResourceException(); //TODO Message
                }
                try {
                    final Factory<? extends T> factory = configurationLoader.load(ref, factoryInterface, classLoader);
                    if (factory == null) {
                        throw new UnresolvableResourceException("No configuration found for node with name=" + name + ",clazz=" + valueInterface.getSimpleName()); //TODO
                    }
                    return cache = factory.produce(dependencies, properties);
                } catch (final ArtifactOfWrongTypeException e) {
                    return cache = configurationLoader.load(ref, valueInterface, classLoader);
                }
            } else if (defaultFactory != null) {
                return cache = defaultFactory.produce(dependencies, properties);
            }
        } catch (final UnresolvableResourceException e) {
            throw e;
        } catch (final Exception e) {
            throw new UnresolvableResourceException(e); //TODO Message
        }
        throw new UnresolvableResourceException("No configuration found for node with name=" + name + ",clazz=" + valueInterface.getSimpleName()); //TODO
    }
}
