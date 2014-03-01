package io.machinecode.nock.seam;

import io.machinecode.nock.spi.loader.ArtifactLoader;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
@Name("seamArtifactLoader")
@Scope(ScopeType.APPLICATION)
@Startup
@AutoCreate
public class SeamArtifactLoader implements ArtifactLoader {
    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) {
        return inject(id, as);
    }

    public static <T> T inject(final String id, final Class<T> as) {
        if (Contexts.isApplicationContextActive()) {
            final Object that = Component.getInstance(id);
            if (that == null) {
                return null;
            }
            return as.isAssignableFrom(that.getClass()) ? as.cast(that) : null;
        } else {
            try {
                Lifecycle.beginCall();
                final Object that = Component.getInstance(id);
                if (that == null) {
                    return null;
                }
                return as.isAssignableFrom(that.getClass()) ? as.cast(that) : null;
            } finally {
                Lifecycle.endCall();
            }
        }
    }
}
