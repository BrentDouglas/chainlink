package io.machinecode.chainlink.inject.spring;

import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.Messages;
import org.jboss.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class SpringArtifactLoader implements ArtifactLoader, ApplicationContextAware {

    private static final Logger log = Logger.getLogger(SpringArtifactLoader.class);

    private ApplicationContext context;

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _loader) {
        try {
            final Object that = context.getBean(id);
            if (as.isAssignableFrom(that.getClass())) {
                return as.cast(that);
            }
        } catch (final BeansException e) {
            log.tracef(Messages.get("CHAINLINK-025001.artifact.loader.not.found"), id, as.getSimpleName());
            return null;
        }
        throw new ArtifactOfWrongTypeException(Messages.format("CHAINLINK-025000.artifact.loader.assignability", id, as.getSimpleName()));
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
