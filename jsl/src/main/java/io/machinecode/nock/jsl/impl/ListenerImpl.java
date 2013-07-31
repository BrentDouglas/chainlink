package io.machinecode.nock.jsl.impl;

import io.machinecode.nock.jsl.api.Listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerImpl extends PropertyReferenceImpl implements Listener {
    public ListenerImpl(final Listener that) {
        super(that);
    }
}
