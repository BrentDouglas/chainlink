package io.machinecode.chainlink.inject.cdi;

import io.machinecode.chainlink.core.inject.ArtifactLoaderImpl;
import io.machinecode.chainlink.core.inject.Injector;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import org.jboss.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class CdiArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(CdiArtifactLoader.class);

    public static final AnnotationLiteral<Default> DEFAULT_ANNOTATION_LITERAL = new AnnotationLiteral<Default>() {};

    private final BeanManagerLookup lookup;
    private BeanManager beanManager;

    private final InjectablesProvider provider;

    private CdiArtifactLoader(final BeanManagerLookup lookup, final BeanManager beanManager) {
        this.lookup = lookup;
        this.beanManager = beanManager;
        this.provider = ArtifactLoaderImpl.loadProvider();
    }

    public CdiArtifactLoader(final BeanManagerLookup lookup) {
        this(lookup, null);
    }

    public CdiArtifactLoader(final BeanManager beanManager) {
        this(null, beanManager);
    }

    public CdiArtifactLoader() {
        this(null, null);
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _loader) throws Exception {
        if (this.beanManager == null) {
            this.beanManager = lookup.lookupBeanManager();
        }
        final T that = _inject(beanManager, as, id, new NamedLiteral(id));
        if (that == null) {
            return null;
        }
        final String name = that.getClass().getName();
        if (!name.contains("_$$_Weld")) { //TODO Also check OWB
            Injector.inject(provider, that);
        }
        return that;
    }

    @Produces
    @Dependent
    @BatchProperty
    public String getBatchProperty(final InjectionPoint injectionPoint) {
        final BatchProperty batchProperty = injectionPoint.getAnnotated().getAnnotation(BatchProperty.class);
        final Member field = injectionPoint.getMember();
        final String property = Injector.property(batchProperty.name(), field.getName(), provider.getInjectables().getProperties());
        if (property == null || "".equals(property)) {
            return null;
        }
        return property;
    }

    @Produces
    @Dependent
    @Default
    public JobContext getJobContext() {
        return provider.getInjectables().getJobContext();
    }

    @Produces
    @Dependent
    @Default
    public StepContext getStepContext() {
        return provider.getInjectables().getStepContext();
    }

    public static <T> T inject(final BeanManager beanManager, final Class<T> as) {
        return _inject(beanManager, as, null, DEFAULT_ANNOTATION_LITERAL);
    }

    static <T> T _inject(final BeanManager beanManager, final Class<T> as, final String id, final Annotation... annotation) {
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
