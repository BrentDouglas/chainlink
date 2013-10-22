package io.machinecode.nock.inject.spring;

import io.machinecode.nock.spi.loader.ArtifactLoader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SpringArtifactLoader implements ArtifactLoader, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader _) {
        try {
            return context.getBean(id, as);
        } catch (final Exception e) {
            return null;
        }
    }

    @Override
    public void setApplicationContext(final ApplicationContext context) throws BeansException {
        this.context = context;
    }
}
