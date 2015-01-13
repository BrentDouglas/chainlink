package io.machinecode.chainlink.core.loader;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.jsl.inherit.InheritableJob;
import io.machinecode.chainlink.spi.util.Messages;

import javax.batch.operations.NoSuchJobException;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class FluentJobLoader extends AbstractJobLoader {

    public FluentJobLoader add(final String jslName, final InheritableJob<?,?,?,?> job) {
        repos.put(jslName, new Node(jslName, job));
        return this;
    }

    final TMap<String, Node> repos = new THashMap<>();

    @Override
    protected Node doLoad(final String jslName) throws NoSuchJobException {
        final Node cached = repos.get(jslName);
        if (cached != null) {
            return cached;
        }
        throw new NoSuchJobException(Messages.format("CHAINLINK-003000.job.loader.no.file", jslName));
    }
}