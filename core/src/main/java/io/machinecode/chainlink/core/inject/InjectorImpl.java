package io.machinecode.chainlink.core.inject;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.inject.Injector;
import org.jboss.logging.Logger;

import java.util.Collections;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InjectorImpl implements Injector {

    private static final Logger log = Logger.getLogger(InjectorImpl.class);

    final Injector injector;
    final Set<Injector> injectors;

    public InjectorImpl(final Injector... injectors) {
        this.injector = new DefaultInjector();
        this.injectors = new TLinkedHashSet<>();
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
