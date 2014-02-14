package io.machinecode.nock.jsl.visitor;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.spi.util.Messages;
import io.machinecode.nock.spi.work.ExecutionWork;
import org.jboss.logging.Logger;

import java.util.Map.Entry;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class JobTraversal {

    private static final Logger log = Logger.getLogger(JobTraversal.class);

    private final String id;
    private final TMap<String, ExecutionWork> transitions;

    public JobTraversal(final String id, final VisitorNode root) {
        this.id = id;
        this.transitions = new THashMap<String, ExecutionWork>(root.ids.size());
        for (final Entry<String, VisitorNode> entry : root.ids.entrySet()) {
            this.transitions.put(entry.getKey(), (ExecutionWork) entry.getValue().value);
        }
    }

    public ExecutionWork next(final String id) {
        final ExecutionWork work = this.transitions.get(id);
        if (work == null) {
            throw new IllegalStateException(Messages.format("NOCK-016000.traversal.cant.resolve.id", this.id, id));
        }
        return work;
    }
}
