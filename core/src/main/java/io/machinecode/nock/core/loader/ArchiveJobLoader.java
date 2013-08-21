package io.machinecode.nock.core.loader;

import io.machinecode.nock.spi.JobLoader;

import java.io.InputStream;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ArchiveJobLoader implements JobLoader {

    @Override
    public InputStream load(final ClassLoader loader, final String id) {
        return loader.getResourceAsStream("META-INF/batch-jobs/" + id + ".xml");
    }
}
