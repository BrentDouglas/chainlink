package io.machinecode.nock.spi;

import java.io.InputStream;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public interface JobLoader {

    InputStream load(final ClassLoader loader, final String id);
}
