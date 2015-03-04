package io.machinecode.chainlink.core.inject;

import io.machinecode.chainlink.core.util.Tccl;
import io.machinecode.chainlink.spi.Messages;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.inject.InjectablesProvider;
import org.jboss.logging.Logger;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class TcclArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(TcclArtifactLoader.class);

    private final InjectablesProvider provider;

    public TcclArtifactLoader(final InjectablesProvider provider) {
        this.provider = provider;
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws Exception {
        final T bean;
        try {
            final Class<?> that = Tccl.get().loadClass(id);
            if (as.isAssignableFrom(that)) {
                bean = as.cast(that.newInstance());
            } else {
                throw new ArtifactOfWrongTypeException(Messages.format("CHAINLINK-025000.artifact.loader.assignability", id, as.getCanonicalName()));
            }
        } catch (final ClassNotFoundException e) {
            log.tracef(Messages.get("CHAINLINK-025001.artifact.loader.not.found"), id, as.getSimpleName());
            return null;
        } catch (final InstantiationException e) {
            log.warnf(Messages.get("CHAINLINK-025002.artifact.loader.instantiation"), id, as.getSimpleName());
            return null;
        } catch (final IllegalAccessException e) {
            log.warnf(Messages.get("CHAINLINK-025003.artifact.loader.access"), id, as.getSimpleName());
            return null;
        }
        Injector.inject(provider, bean);
        return bean;
    }
}
