package io.machinecode.chainlink.inject.cdi;

import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.BeforeShutdown;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CdiArtifactLoader implements ArtifactLoader, Extension {

    private static final Logger log = Logger.getLogger(CdiArtifactLoader.class);

    public static final AnnotationLiteral<Default> DEFAULT_ANNOTATION_LITERAL = new AnnotationLiteral<Default>() {};

    static BeanManager beanManager;

    void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery beforeBeanDiscovery, final BeanManager beanManager) {
        CdiArtifactLoader.beanManager = beanManager;
    }

    public void shutdown(@Observes BeforeShutdown beforeShutdown) {
        beanManager = null;
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _loader) {
        return _inject(beanManager, as, id, new NamedLiteral(id));
    }

    public static <T> T inject(final BeanManager beanManager, final Class<T> as) {
        return _inject(beanManager, as, null, DEFAULT_ANNOTATION_LITERAL);
    }

    private static <T> T _inject(final BeanManager beanManager, final Class<T> as, final String id, final Annotation... annotation) {
        final Set<Bean<?>> beans = beanManager.getBeans(as, annotation);
        final Bean<?> bean = beanManager.resolve(beans);
        if (bean == null) {
            log.tracef(Messages.get("CHAINLINK-025001.artifact.loader.not.found"), id, as.getSimpleName());
            return null;
        }
        final CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
        return as.cast(beanManager.getReference(bean, as, ctx));
    }
}
