package io.machinecode.nock.core.loader;

import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.loader.ArtifactOfWrongTypeException;
import io.machinecode.nock.spi.util.Messages;
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
                throw new ArtifactOfWrongTypeException(Messages.format("NOCK-025000.artifact.loader.assignability", id, as.getCanonicalName()));
            }
        } catch (final ClassNotFoundException e) {
            log.tracef(Messages.get("NOCK-025001.artifact.loader.not.found"), id, as.getSimpleName());
        } catch (final InstantiationException e) {
            log.warnf(Messages.get("NOCK-025002.artifact.loader.instantiation"), id, as.getSimpleName());
        } catch (final IllegalAccessException e) {
            log.warnf(Messages.get("NOCK-025003.artifact.loader.access"), id, as.getSimpleName());
        }
        return null;
    }
}
