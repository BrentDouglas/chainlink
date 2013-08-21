package io.machinecode.nock.core.descriptor.task;

import io.machinecode.nock.spi.element.Properties;
import io.machinecode.nock.spi.element.task.ItemReader;
import io.machinecode.nock.core.descriptor.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderImpl extends PropertyReferenceImpl implements ItemReader {

    public ItemReaderImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
