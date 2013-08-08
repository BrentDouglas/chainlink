package io.machinecode.nock.core.model.task;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.task.ItemReader;
import io.machinecode.nock.core.model.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemReaderImpl extends PropertyReferenceImpl implements ItemReader {

    public ItemReaderImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
