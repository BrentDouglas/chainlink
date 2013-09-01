package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.element.task.ItemWriter;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemWriter> implements ItemWriter {

    public ItemWriterImpl(final String ref, final PropertiesImpl properties) {
        super(new TypedArtifactReference<javax.batch.api.chunk.ItemWriter>(ref, javax.batch.api.chunk.ItemWriter.class), properties);
    }
}
