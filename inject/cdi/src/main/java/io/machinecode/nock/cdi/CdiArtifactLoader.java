package io.machinecode.nock.cdi;

import io.machinecode.nock.spi.loader.ArtifactLoader;

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

    public static final AnnotationLiteral<Default> DEFAULT_ANNOTATION_LITERAL = new AnnotationLiteral<Default>() {};

    private static BeanManager beanManager;

    void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery beforeBeanDiscovery, final BeanManager beanManager) {
        CdiArtifactLoader.beanManager = beanManager;
        beforeBeanDiscovery.addAnnotatedType(
                beanManager.createAnnotatedType(CdiProducer.class)
        );
    }

    public void shutdown(@Observes BeforeShutdown beforeShutdown) {
        beanManager = null;
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _) {
        return _inject(beanManager, as, new NamedLiteral(id));
    }

    public static <T> T inject(final BeanManager beanManager, final Class<T> as) {
        return _inject(beanManager, as, DEFAULT_ANNOTATION_LITERAL);
    }

    private static <T> T _inject(final BeanManager beanManager, final Class<T> as, final Annotation... annotation) {
        final Set<Bean<?>> beans = beanManager.getBeans(as, annotation);
        final Bean<?> bean = beanManager.resolve(beans);
        final CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
        return as.cast(beanManager.getReference(bean, as, ctx));
    }
}
