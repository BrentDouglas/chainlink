package io.machinecode.nock.core.model.task;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.task.ItemProcessor;
import io.machinecode.nock.core.model.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ItemProcessorImpl extends PropertyReferenceImpl implements ItemProcessor {

    public ItemProcessorImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
