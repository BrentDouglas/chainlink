package io.machinecode.nock.core.model.task;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.task.ItemWriter;
import io.machinecode.nock.core.model.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemWriterImpl extends PropertyReferenceImpl implements ItemWriter {

    public ItemWriterImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
