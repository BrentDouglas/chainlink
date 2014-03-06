package io.machinecode.chainlink.core.validation;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.work.ExecutionWork;
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
        this.transitions = new THashMap<String, ExecutionWork>(root.getIds().size());
        for (final Entry<String, VisitorNode> entry : root.getIds().entrySet()) {
            this.transitions.put(entry.getKey(), (ExecutionWork) entry.getValue().getValue());
        }
    }

    public ExecutionWork next(final String id) {
        final ExecutionWork work = this.transitions.get(id);
        if (work == null) {
            throw new IllegalStateException(Messages.format("CHAINLINK-016000.traversal.cant.resolve.id", this.id, id));
        }
        return work;
    }
}
