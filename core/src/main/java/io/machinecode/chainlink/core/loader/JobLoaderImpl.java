package io.machinecode.chainlink.core.loader;

import gnu.trove.set.hash.TLinkedHashSet;
import io.machinecode.chainlink.jsl.xml.loader.JarXmlJobLoader;
import io.machinecode.chainlink.jsl.xml.loader.WarXmlJobLoader;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.loader.JobLoader;
import io.machinecode.chainlink.spi.util.Messages;
import org.jboss.logging.Logger;

import javax.batch.operations.NoSuchJobException;
import java.util.Collections;
import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public class JobLoaderImpl implements JobLoader {

    private static final Logger log = Logger.getLogger(JobLoaderImpl.class);

    private final JarXmlJobLoader jarLoader;
    private final WarXmlJobLoader warLoader;
    private final Set<JobLoader> loaders;

    public JobLoaderImpl(final ClassLoader classLoader, final JobLoader... jobLoaders) {
        this.jarLoader = new JarXmlJobLoader(classLoader);
        this.warLoader = new WarXmlJobLoader(classLoader);
        this.loaders = new TLinkedHashSet<JobLoader>();
        Collections.addAll(this.loaders, jobLoaders);
    }

    @Override
    public Job load(final String jslName) throws NoSuchJobException {
        // 1. Provided Loaders
        for (final JobLoader loader : this.loaders) {
            try {
                return loader.load(jslName);
            } catch (final NoSuchJobException e) {
                log.tracef(Messages.get("CHAINLINK-003100.job.loader.not.found"), jslName, loader.getClass().getSimpleName());
            }
        }
        // 2. Archive Loaders
        try {
            return jarLoader.load(jslName);
        } catch (final NoSuchJobException e) {
            log.tracef(Messages.get("CHAINLINK-003100.job.loader.not.found"), jslName, jarLoader.getClass().getSimpleName());
        }
        return warLoader.load(jslName);
    }
}
