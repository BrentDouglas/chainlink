package io.machinecode.nock.core.inject;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.inject.Injector;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InjectorImpl implements Injector {

    private static final Logger log = Logger.getLogger(InjectorImpl.class);

    final Injector injector;
    final Set<Injector> injectors;

    public InjectorImpl(final Configuration configuration) {
        this.injector = new DefaultInjector();
        this.injectors = new TLinkedHashSet<Injector>();
        Collections.addAll(this.injectors, configuration.getInjectors());
        //final List<Injector> loaders;
        //try {
        //    loaders = new ResolvableService<Injector>(Injector.class).resolve(configuration.getClassLoader());
        //} catch (final ClassNotFoundException e) {
        //    throw new RuntimeException(e);
        //}
        //this.injectors.addAll(loaders);
    }

    @Override
    public <T> boolean inject(final T bean) throws Exception {
        if (bean == null) {
            log.debug("Trying to inject null bean", new Exception()); //TODO Message
            return false;
        }
        for (final Injector injector : injectors) {
            if (injector.inject(bean)) {
                return true;
            }
        }
        return injector.inject(bean);
    }
}
