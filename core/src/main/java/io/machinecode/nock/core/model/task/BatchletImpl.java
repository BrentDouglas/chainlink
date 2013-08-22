package io.machinecode.nock.core.model.task;

import io.machinecode.nock.core.model.PropertiesImpl;
import io.machinecode.nock.core.model.PropertyReferenceImpl;
import io.machinecode.nock.core.inject.ResolvableReference;
import io.machinecode.nock.spi.element.task.Batchlet;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletImpl extends PropertyReferenceImpl<javax.batch.api.Batchlet> implements Batchlet {

    public BatchletImpl(final String ref, final PropertiesImpl properties) {
        super(new ResolvableReference<javax.batch.api.Batchlet>(ref, javax.batch.api.Batchlet.class), properties);
    }
}
