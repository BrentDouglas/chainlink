package io.machinecode.nock.jsl.impl.partition;

import io.machinecode.nock.jsl.api.partition.Collector;
import io.machinecode.nock.jsl.impl.PropertyReferenceImpl;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CollectorImpl extends PropertyReferenceImpl implements Collector {
    public CollectorImpl(final Collector that) {
        super(that);
    }
}
