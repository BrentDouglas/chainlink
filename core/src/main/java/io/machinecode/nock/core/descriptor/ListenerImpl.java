package io.machinecode.nock.core.descriptor;

import io.machinecode.nock.spi.element.Listener;
import io.machinecode.nock.spi.element.Properties;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerImpl extends PropertyReferenceImpl implements Listener {

    public ListenerImpl(final String ref, final Properties properties) {
        super(ref, properties);
    }
}
