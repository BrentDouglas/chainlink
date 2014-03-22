package io.machinecode.chainlink.core.inject;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.chainlink.inject.core.DefaultInjector;
import io.machinecode.chainlink.spi.configuration.Configuration;
import io.machinecode.chainlink.spi.inject.Injector;
import io.machinecode.chainlink.spi.util.Messages;
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

    public InjectorImpl(final Injector... injectors) {
        this.injector = new DefaultInjector();
        this.injectors = new TLinkedHashSet<Injector>();
        Collections.addAll(this.injectors, injectors);
    }

    @Override
    public boolean inject(final Object bean) throws Exception {
        if (bean == null) {
            log.debugf(Messages.get("CHAINLINK-000001.injector.bean.null"));
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
