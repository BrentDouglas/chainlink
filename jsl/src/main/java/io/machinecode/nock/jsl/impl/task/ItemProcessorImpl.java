package io.machinecode.nock.jsl.impl.task;

import io.machinecode.nock.jsl.api.task.ItemProcessor;
import io.machinecode.nock.jsl.impl.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorImpl extends PropertyReferenceImpl implements ItemProcessor {
    public ItemProcessorImpl(final ItemProcessor that) {
        super(that);
    }
}
