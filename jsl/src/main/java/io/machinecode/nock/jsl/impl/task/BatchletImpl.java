package io.machinecode.nock.jsl.impl.task;

import io.machinecode.nock.jsl.api.task.Batchlet;
import io.machinecode.nock.jsl.impl.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class BatchletImpl extends PropertyReferenceImpl implements Batchlet {

    public BatchletImpl(final Batchlet that) {
        super(that);
    }
}
