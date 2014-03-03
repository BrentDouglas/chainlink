package io.machinecode.nock.inject.spring;

import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.ArtifactOfWrongTypeException;
import io.machinecode.nock.spi.util.Messages;
import org.jboss.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SpringArtifactLoader implements ArtifactLoader, ApplicationContextAware {

    private static final Logger log = Logger.getLogger(SpringArtifactLoader.class);

    private ApplicationContext context;

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _) {
        try {
            final Object that = context.getBean(id);
            if (as.isAssignableFrom(that.getClass())) {
                return as.cast(that);
            }
            throw new ArtifactOfWrongTypeException(Messages.format("NOCK-025000.artifact.loader.assignability", id, as.getCanonicalName()));
        } catch (final Exception e) {
            log.tracef(Messages.get("NOCK-025001.artifact.loader.not.found"), id, as.getSimpleName());
            return null;
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
