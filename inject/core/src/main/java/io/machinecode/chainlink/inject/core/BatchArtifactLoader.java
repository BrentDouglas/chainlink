package io.machinecode.chainlink.inject.core;

import io.machinecode.chainlink.inject.core.batch.BatchArtifactRef;
import io.machinecode.chainlink.inject.core.batch.BatchArtifacts;
import io.machinecode.chainlink.spi.inject.ArtifactLoader;
import io.machinecode.chainlink.spi.inject.ArtifactOfWrongTypeException;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.batch.operations.BatchRuntimeException;
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
public abstract class BatchArtifactLoader implements ArtifactLoader {

    private static final Logger log = Logger.getLogger(BatchArtifactLoader.class);

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

    public BatchArtifactLoader(final ClassLoader loader) {
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
        for (final BatchArtifactRef ref : artifacts) {
            if (ref.getId() == null) {
                throw new IllegalStateException(Messages.format("CHAINLINK-025005.artifact.batch.file.invalid", "id"));
            }
            if (ref.getClazz() == null) {
                throw new IllegalStateException(Messages.format("CHAINLINK-025005.artifact.batch.file.invalid", "class"));
            }
        }
    }

    @Override
    public <T> T load(final String id, final Class<T> as, final ClassLoader loader) throws BatchRuntimeException {
        String fqcn = null;
        for (final BatchArtifactRef ref : artifacts) {
            if (ref.getId().equals(id)) {
                fqcn = ref.getClazz();
            }
        }
        if (fqcn == null) {
             return null;
        }
        final Class<?> clazz;
        try {
            clazz = loader.loadClass(fqcn);
        } catch (final ClassNotFoundException e) {
            log.errorf(e, Messages.get("CHAINLINK-002200.validation.cant.load.matching.artifact"), id, fqcn);
            return null;
        }

        final Object that;
        try {
            that = clazz.newInstance();
        } catch (final Exception e) {
            log.errorf(e, Messages.get("CHAINLINK-002200.validation.cant.load.matching.artifact"), id, fqcn);
            return null;
        }
        if (!as.isAssignableFrom(that.getClass())) {
            throw new ArtifactOfWrongTypeException(Messages.format("CHAINLINK-025000.artifact.loader.assignability", id, as.getSimpleName()));
        }
        return as.cast(that);
    }
}
