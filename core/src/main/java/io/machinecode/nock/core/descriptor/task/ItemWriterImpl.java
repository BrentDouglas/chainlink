package io.machinecode.nock.core.descriptor.task;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.task.ItemWriter;
import io.machinecode.nock.core.descriptor.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterImpl extends PropertyReferenceImpl implements ItemWriter {

    public ItemWriterImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
