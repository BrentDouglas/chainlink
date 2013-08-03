package io.machinecode.nock.jsl.impl.task;

import io.machinecode.nock.jsl.api.task.ItemReader;
import io.machinecode.nock.jsl.impl.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderImpl extends PropertyReferenceImpl implements ItemReader {
    public ItemReaderImpl(final ItemReader that) {
        super(that);
    }
}
