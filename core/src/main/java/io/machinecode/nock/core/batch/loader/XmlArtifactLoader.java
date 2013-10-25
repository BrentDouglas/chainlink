package io.machinecode.nock.core.batch.loader;

import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.core.batch.BatchArtifactRef;
import io.machinecode.nock.core.batch.BatchArtifacts;
import io.machinecode.nock.spi.loader.ArtifactLoader;
import io.machinecode.nock.spi.util.Message;
import org.jboss.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class XmlArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(XmlArtifactLoader.class);

    private static final Unmarshaller unmarshaller;

    static {
        final JAXBContext context;
        try {
            context = JAXBContext.newInstance(BatchArtifacts.class);
            unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private final List<BatchArtifactRef> artifacts;

    public abstract String getPrefix();

    public XmlArtifactLoader(final ClassLoader loader) {
        final BatchArtifacts batchArtifacts;
        final InputStream stream = loader.getResourceAsStream(getPrefix() + "batch.xml");
        if (stream == null) {
            this.artifacts = Collections.emptyList();
            return;
        }
        try {
            batchArtifacts = (BatchArtifacts) unmarshaller.unmarshal(stream);
        } catch (final JAXBException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stream.close();
            } catch (final IOException e) {
                //
            }
        }
        this.artifacts = batchArtifacts.getRefs();
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) {
        final THashSet<String> fqcns = new THashSet<String>();
        for (final BatchArtifactRef ref : artifacts) {
            if (ref.getId().equals(id)) {
                fqcns.add(ref.getClazz());
            }
        }
        if (fqcns.isEmpty()) {
             return null;
        }
        for (final String fqcn : fqcns) {
            final Class<?> clazz;
            try {
                clazz = loader.loadClass(fqcn);
            } catch (final ClassNotFoundException e) {
                log.error(Message.cantLoadMatchingArtifact(id, fqcn), e);
                continue;
            }

            final Object that;
            try {
                that = clazz.newInstance();
            } catch (final Exception e) {
                log.error(Message.cantLoadMatchingArtifact(id, fqcn), e);
                continue;
            }
            if (!as.isAssignableFrom(that.getClass())) {
                log.warn(Message.artifactWithWrongClass(id, fqcn, as.getCanonicalName()));
                continue;
            }
            return as.cast(that);
        }
        return null;
    }
}
