package io.machinecode.chainlink.core.validation;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.core.jsl.impl.execution.ExecutionImpl;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.Messages;
import org.jboss.logging.Logger;

import java.io.Serializable;
import java.util.Map.Entry;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public final class JobTraversal implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(JobTraversal.class);

    private final String id;
    private final TMap<String, ExecutionImpl> transitions;

    public JobTraversal(final String id, final VisitorNode root) {
        this.id = id;
        this.transitions = new THashMap<>(root.getIds().size());
        for (final Entry<String, VisitorNode> entry : root.getIds().entrySet()) {
            this.transitions.put(entry.getKey(), (ExecutionImpl) entry.getValue().getValue());
        }
    }

    public ExecutionImpl next(final String id) {
        final ExecutionImpl work = this.transitions.get(id);
        if (work == null) {
            throw new IllegalStateException(Messages.format("CHAINLINK-016000.traversal.cant.resolve.id", this.id, id));
        }
        return work;
    }
}
