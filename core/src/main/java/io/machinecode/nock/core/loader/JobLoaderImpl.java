package io.machinecode.nock.core.loader;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.nock.jsl.loader.JarXmlJobLoader;
import io.machinecode.nock.spi.configuration.Configuration;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.loader.JobLoader;
import io.machinecode.nock.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.batch.operations.NoSuchJobException;
import java.util.Collections;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobLoaderImpl implements JobLoader {

    private static final Logger log = Logger.getLogger(JobLoaderImpl.class);

    private final JarXmlJobLoader loader;
    private final Set<JobLoader> loaders;

    public JobLoaderImpl(final Configuration configuration) {
        this.loader = new JarXmlJobLoader(configuration.getClassLoader());
        this.loaders = new TLinkedHashSet<JobLoader>();
        Collections.addAll(this.loaders, configuration.getJobLoaders());
    }

    @Override
    public Job load(final String jslName) throws NoSuchJobException {
        // 1. Provided Loaders
        for (final JobLoader loader : this.loaders) {
            try {
                return loader.load(jslName);
            } catch (final NoSuchJobException e) {
                log.tracef(Messages.get("NOCK-003100.job.loader.not.found"), jslName, loader.getClass().getSimpleName());
            }
        }
        // 2. Archive Loader
        return loader.load(jslName);
    }
}
