package io.machinecode.chainlink.inject.cdi;

import javax.enterprise.inject.spi.BeanManager;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public interface BeanManagerLookup {

    BeanManager lookupBeanManager() throws Exception;
}
