package io.machinecode.nock.jsl.loader;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.jsl.inherit.InheritableJob;

import javax.batch.operations.NoSuchJobException;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FluentJobLoader extends AbstractJobLoader {


    public FluentJobLoader add(final String jslName, final InheritableJob<?,?,?,?> job) {
        repos.put(jslName, new Node(jslName, job));
        return this;
    }

    final TMap<String, Node> repos = new THashMap<String, Node>();

    @Override
    protected Node doLoad(final String id) throws NoSuchJobException {
        final Node cached = repos.get(id);
        if (cached != null) {
            return cached;
        }
        throw new NoSuchJobException();
    }
}
