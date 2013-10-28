package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.element.task.ItemReader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemReader> implements ItemReader {

    public ItemReaderImpl(final TypedArtifactReference<javax.batch.api.chunk.ItemReader> ref, final PropertiesImpl properties) {
        super(ref, properties);
    }
}
