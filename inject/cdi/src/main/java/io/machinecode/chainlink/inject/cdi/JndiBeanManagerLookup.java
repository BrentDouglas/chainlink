package io.machinecode.chainlink.inject.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;

/**
* @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
* @since 1.0
*/
class JndiBeanManagerLookup implements BeanManagerLookup {
    @Override
    public BeanManager lookupBeanManager() throws Exception {
        return InitialContext.doLookup("java:comp/BeanManager");
    }
}
