package io.machinecode.nock.jsl.validation;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class TransitionContext {

    private final String element;
    private final String id;
    private final String next;
    private final List<TransitionContext> children = new LinkedList<TransitionContext>();
    private boolean failed;
    private List<TransitionContext> cycle;
    private final Map<String, TransitionContext> jump;

    public TransitionContext() {
        this.element = null;
        this.id = null;
        this.next = null;
        this.jump = new THashMap<String, TransitionContext>();
    }

    public TransitionContext(final String element, final String id, final String next, final TransitionContext parent) {
        this.element = element;
        this.id = id;
        this.next = next;
        this.jump = parent.jump;
        this.jump.put(id, this);
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
            while ((that = jump.get(that.next)) != null) {
                cycle.add(that);
                if (!trail.add(that.id)) {
                    this.cycle = cycle;
                    this.failed = true;
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

    public final StringBuilder toTree(final StringBuilder builder) {
        if (this.failed) {
            builder.append("Cycle detected:")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
            for (final TransitionContext problem : this.cycle) {
                builder.append(problem.id)
                        .append(" -> ")
                        .append(problem.next)
                        .append(" (")
                        .append(problem.element)
                        .append(')')
                        .append(System.lineSeparator());
            }
            builder.append(System.lineSeparator())
                    .append(System.lineSeparator());
        }
        for (final TransitionContext context : children) {
            context.toTree(builder);
        }
        return builder;
    }

    public void addChild(final TransitionContext child) {
        this.children.add(child);
    }
}
