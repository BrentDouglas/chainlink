package io.machinecode.nock.jsl.validation;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.jsl.api.execution.Flow;
import io.machinecode.nock.jsl.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class TransitionContext extends ValidationContext<TransitionContext> {

    private final String id;
    private final String next;
    private final Map<String, TransitionContext> jobScope;
    private final Map<String, TransitionContext> localScope;
    private final List<Pair<String, String>> transitions;

    public TransitionContext(final String element, final String id) {
        super(element);
        this.id = id;
        this.next = null;
        this.jobScope = new THashMap<String, TransitionContext>(0);
        this.localScope = this.jobScope;
        this.transitions = new ArrayList<Pair<String, String>>(0);
    }

    public TransitionContext(final String element, final String id, final String next, final TransitionContext parent) {
        super(element, parent);
        this.id = id;
        this.next = next;
        this.transitions = new ArrayList<Pair<String, String>>(0);
        if (Flow.ELEMENT.equals(element)) {
            this.localScope = new THashMap<String, TransitionContext>(0);
        } else {
            this.localScope = parent.localScope;
        }
        if (parent.localScope != this.localScope) {
            parent.localScope.put(id, this);
        }
        this.jobScope = parent.jobScope;
        this.jobScope.put(id, this);
        parent.addTransition(element, next);
    }

    void addTransition(final String element, final String next) {
        if (next != null) {
            this.transitions.add(Pair.of(element, next));
        }
    }

    public final boolean hasCycle() {
        final List<TransitionContext> cycle = new LinkedList<TransitionContext>();
        final Set<String> trail = new THashSet<String>();
        boolean ret = false;
        //Root node
        if (this.id != null) {
            TransitionContext that = this;
            cycle.add(that);
            trail.add(that.id);
            while ((that = jobScope.get(that.next)) != null) {
                cycle.add(that);
                if (!trail.add(that.id)) {
                    addProblem(Problem.cycleDetected());
                    for (final TransitionContext problem : cycle) {
                        final StringBuilder builder = new StringBuilder()
                                .append("  ")
                                .append(problem.id)
                                .append(" -> ")
                                .append(problem.next);
                        addProblem(problem.element(builder).toString());
                    }
                    final StringBuilder builder = new StringBuilder()
                            .append("  ")
                            .append(that.id)
                            .append(" -> ")
                            .append(that.next);
                    addProblem(that.element(builder).toString());
                    ret = true;
                    break;
                }
            }
        }

        for (final TransitionContext child : children) {
            ret = child.hasCycle() || ret;
        }

        return ret;
    }

    public final boolean hasInvalidTransfer() {
        boolean ret = false;
        //Root node
        TransitionContext that = this;
        for (final Pair<String, String> entry : transitions) {
            if (!localScope.containsKey(entry.getValue())) {
                addProblem(Problem.invalidTransition(that.id, entry.getKey(), entry.getValue()));
                ret = true;
            }
        }

        for (final TransitionContext child : children) {
            ret = child.hasInvalidTransfer() || ret;
        }

        return ret;
    }

    @Override
    protected StringBuilder element(final StringBuilder builder) {
        builder.append('<')
                .append(element);
        if (id != null) {
            builder.append(" id=\"")
                    .append(id)
                    .append('"');
        }
        if (next != null) {
            builder.append(" next=\"")
                    .append(next)
                    .append('"');
        }
        return builder.append('>');
    }
}
