package io.machinecode.nock.core.loader;

import io.machinecode.nock.spi.loader.ArtifactLoader;
import org.jboss.logging.Logger;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class TcclArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(TcclArtifactLoader.class);

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) {
        try {
            final Class<?> that = Thread.currentThread().getContextClassLoader().loadClass(id);
            if (as.isAssignableFrom(that)) {
                return as.cast(that.newInstance());
            } else {
                log.warn(""); //TODO Message
            }
        } catch (final ClassNotFoundException e) {
            //
        } catch (final InstantiationException e) {
            log.warn(""); //TODO Message
        } catch (final IllegalAccessException e) {
            log.warn(""); //TODO Message
        }
        return null;
    }
}
