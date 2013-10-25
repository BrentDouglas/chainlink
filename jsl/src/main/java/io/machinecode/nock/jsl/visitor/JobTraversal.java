package io.machinecode.nock.jsl.visitor;

import gnu.trove.map.TMap;
import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.jsl.util.ImmutablePair;
import io.machinecode.nock.spi.util.Pair;
import io.machinecode.nock.spi.work.ExecutionWork;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class JobTraversal {

    private final TMap<String, Pair<ExecutionWork, String>> transitions;

    public JobTraversal(final VisitorNode root) {
        this.transitions = new THashMap<String, Pair<ExecutionWork, String>>(root.transitions.size());
        for (final Transition that : root.transitions) {
            final VisitorNode next = root.ids.get(that.id);
            this.transitions.put(that.id, ImmutablePair.of(next.value, that.next));
        }
    }

    public ExecutionWork next(final String next) {
        final Pair<ExecutionWork, String> pair =  this.transitions.get(next);
        return pair == null ? null : pair.getName();
    }
}
