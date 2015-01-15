package io.machinecode.chainlink.inject.cdi;

import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CdiArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(CdiArtifactLoader.class);

    public static final AnnotationLiteral<Default> DEFAULT_ANNOTATION_LITERAL = new AnnotationLiteral<Default>() {};

    private final BeanManagerLookup lookup;
    private BeanManager beanManager;

    public CdiArtifactLoader(final BeanManagerLookup lookup) {
        this.lookup = lookup;
    }

    public CdiArtifactLoader(final BeanManager beanManager) {
        this.beanManager = beanManager;
        this.lookup = null;
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _loader) throws Exception {
        if (this.beanManager == null) {
            this.beanManager = lookup.lookupBeanManager();
        }
        return _inject(beanManager, as, id, new NamedLiteral(id));
    }

    public static <T> T inject(final BeanManager beanManager, final Class<T> as) {
        return _inject(beanManager, as, null, DEFAULT_ANNOTATION_LITERAL);
    }

    private static <T> T _inject(final BeanManager beanManager, final Class<T> as, final String id, final Annotation... annotation) {
        final Set<Bean<?>> beans = beanManager.getBeans(as, annotation);
        final Bean<?> bean = beanManager.resolve(beans);
        if (bean == null) {
            if (id != null && !beanManager.getBeans(id).isEmpty()) {
                throw new ArtifactOfWrongTypeException(Messages.format("CHAINLINK-025000.artifact.loader.assignability", id, as.getSimpleName()));
            }
            log.tracef(Messages.get("CHAINLINK-025001.artifact.loader.not.found"), id, as.getSimpleName());
            return null;
        }
        final CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
        return as.cast(beanManager.getReference(bean, as, ctx));
    }
}
