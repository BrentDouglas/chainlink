package io.machinecode.nock.jsl.impl.chunk;

import io.machinecode.nock.jsl.api.chunk.ItemWriter;
import io.machinecode.nock.jsl.impl.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterImpl extends PropertyReferenceImpl implements ItemWriter {
    public ItemWriterImpl(final ItemWriter that) {
        super(that);
    }
}
