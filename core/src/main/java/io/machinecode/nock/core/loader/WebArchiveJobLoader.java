package io.machinecode.nock.core.loader;

import io.machinecode.nock.spi.JobLoader;

import java.io.InputStream;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class WebArchiveJobLoader implements JobLoader {

    @Override
    public InputStream load(final ClassLoader loader, final String id) {
        return loader.getResourceAsStream("WEB-INF/classes/META-INF/batch-jobs/" + id + ".xml");
    }
}
