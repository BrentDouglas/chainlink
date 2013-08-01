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
public class CycleContext {

    private final String element;
    private final String id;
    private final String next;
    private final List<CycleContext> children = new LinkedList<CycleContext>();
    private boolean failed;
    private List<CycleContext> cycle;
    private final Map<String, CycleContext> jump;

    public CycleContext() {
        this.element = null;
        this.id = null;
        this.next = null;
        this.jump = new THashMap<String, CycleContext>();
    }

    public CycleContext(final String element, final String id, final String next, final CycleContext parent) {
        this.element = element;
        this.id = id;
        this.next = next;
        this.jump = parent.jump;
        this.jump.put(id, this);
    }

    public final boolean hasCycle() {
        final List<CycleContext> cycle = new LinkedList<CycleContext>();
        final Set<String> trail = new THashSet<String>();
        boolean ret = false;
        //Root node
        if (this.id != null) {
            CycleContext that = this;
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

        for (final CycleContext child : children) {
            ret = child.hasCycle() || ret;
        }

        return ret;
    }

    public final StringBuilder toTree(final StringBuilder builder) {
        if (this.failed) {
            builder.append("Cycle detected:")
                    .append(System.lineSeparator())
                    .append(System.lineSeparator());
            for (final CycleContext problem : this.cycle) {
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
        for (final CycleContext context : children) {
            context.toTree(builder);
        }
        return builder;
    }

    public void addChild(final CycleContext child) {
        this.children.add(child);
    }
}
