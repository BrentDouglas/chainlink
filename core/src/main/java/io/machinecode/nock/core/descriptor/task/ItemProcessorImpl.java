package io.machinecode.nock.core.descriptor.task;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.task.ItemProcessor;
import io.machinecode.nock.core.descriptor.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorImpl extends PropertyReferenceImpl implements ItemProcessor {

    public ItemProcessorImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
