package io.machinecode.chainlink.inject.cdi;

import org.jboss.logging.Logger;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CdiExtension implements Extension {

    private static final Logger log = Logger.getLogger(CdiExtension.class);

    static BeanManager beanManager;

    void afterBeanDiscovery(@Observes final AfterBeanDiscovery event, final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    public void shutdown(@Observes BeforeShutdown event) {
        beanManager = null;
    }

    void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery event, final BeanManager beanManager) {
        event.addAnnotatedType(beanManager.createAnnotatedType(CdiArtifactLoader.class));
    }
}
