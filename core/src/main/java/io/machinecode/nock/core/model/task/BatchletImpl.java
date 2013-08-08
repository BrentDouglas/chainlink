package io.machinecode.nock.core.model.task;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.task.Batchlet;
import io.machinecode.nock.core.model.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletImpl extends PropertyReferenceImpl implements Batchlet {

    public BatchletImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
