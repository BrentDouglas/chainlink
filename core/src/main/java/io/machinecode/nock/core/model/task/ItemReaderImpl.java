package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.element.task.ItemReader;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemReader> implements ItemReader {

    public ItemReaderImpl(final String ref, final PropertiesImpl properties) {
        super(new TypedArtifactReference<javax.batch.api.chunk.ItemReader>(ref, javax.batch.api.chunk.ItemReader.class), properties);
    }
}
