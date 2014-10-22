package io.machinecode.chainlink.ee.wildfly.cdi;

import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com>Brent Douglas</a>
 * @since 1.0
 */
public class WildflyCdiArtifacyLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(WildflyCdiArtifacyLoader.class);

    private final BeanManager beanManager;

    public WildflyCdiArtifacyLoader(final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _loader) {
        final Set<Bean<?>> beans = beanManager.getBeans(as);
        final Bean<?> bean = beanManager.resolve(beans);
        if (bean == null) {
            log.tracef(Messages.get("CHAINLINK-025001.artifact.loader.not.found"), new NamedLiteral(id), as.getSimpleName());
            return null;
        }
        final CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
        return as.cast(beanManager.getReference(bean, as, ctx));
    }
}
