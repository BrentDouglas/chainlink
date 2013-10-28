package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.loader.TypedArtifactReference;
import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.spi.element.task.ItemProcessor;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorImpl extends PropertyReferenceImpl<javax.batch.api.chunk.ItemProcessor> implements ItemProcessor {

    public ItemProcessorImpl(final TypedArtifactReference<javax.batch.api.chunk.ItemProcessor> ref, final PropertiesImpl properties) {
        super(ref, properties);
    }
}
