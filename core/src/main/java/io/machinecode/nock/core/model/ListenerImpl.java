package io.machinecode.nock.core.model;

import io.machinecode.nock.jsl.api.Listener;
import io.machinecode.nock.jsl.api.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerImpl extends PropertyReferenceImpl implements Listener {

    public ListenerImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
