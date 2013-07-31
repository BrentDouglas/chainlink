package io.machinecode.nock.jsl.impl.chunk;

import io.machinecode.nock.jsl.api.chunk.ItemReader;
import io.machinecode.nock.jsl.impl.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderImpl extends PropertyReferenceImpl implements ItemReader {
    public ItemReaderImpl(final ItemReader that) {
        super(that);
    }
}
