package io.machinecode.chainlink.core.validation.visitor;

import gnu.trove.map.hash.THashMap;
import io.machinecode.chainlink.spi.element.Element;
import io.machinecode.chainlink.spi.element.Job;
import io.machinecode.chainlink.spi.element.execution.Execution;
import io.machinecode.chainlink.spi.element.execution.Flow;
import io.machinecode.chainlink.spi.element.execution.TransitionExecution;
import io.machinecode.chainlink.spi.util.Messages;
import io.machinecode.chainlink.spi.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class VisitorNode {

    public static final String ERROR = "FAILURE -";
    public static final String PLAIN = "         ";

    protected final Map<String, VisitorNode> ids;
    protected final List<String> problems = new ArrayList<>(0);
    protected final List<Cycle> cycles = new ArrayList<>(0);
    protected final VisitorNode parent;
    protected final List<VisitorNode> children = new ArrayList<>(0);
    protected final String elementName;
    protected final Element element;
    protected final Execution value;
    protected boolean failed = false;

    protected String id;
    protected String next;
    protected final Map<String, VisitorNode> _executions;
    protected final List<Transition> transitions = new ArrayList<>(0);


    public VisitorNode(final String elementName, final Element value) {
        if (!(value instanceof Job)) {
            throw new IllegalStateException(); //TODO Message
        }
        this.elementName = elementName;
        this.element = value;
        this.id = ((Job)value).getId();
        this.value = null;
        this.parent = null;
        this.ids = new THashMap<>(0);
        this._executions = new THashMap<>(0);
    }

    public VisitorNode(final String elementName, final Element value, final VisitorNode parent) {
        boolean ex = value instanceof Execution;
        this.elementName = elementName;
        this.element = value;
        this.parent = parent;
        this.ids = parent.ids;
        if (value instanceof Flow) {
            this.value = (Execution)value;
            this.id = this.value.getId();
            this._executions = new THashMap<>(0);
        } else if (ex) {
            this.value = (Execution)value;
            this.id = this.value.getId();
            this._executions = parent._executions;
        } else {
            this.value = null;
            this._executions = parent._executions;
        }
        if (this.id != null) {
            final VisitorNode old;
            if ((old = this.ids.put(this.id, this)) != null) {
                addProblem(Messages.format("CHAINLINK-002105.validation.non.unique.id", this.id));
                old.addProblem(Messages.format("CHAINLINK-002105.validation.non.unique.id", this.id));
            }
        }
        if (value instanceof TransitionExecution) {
            this.next = ((TransitionExecution) value).getNext();
            if (this.next != null) {
                this.transitions.add(new Transition(Transition.Type.SIBLING, this.elementName, this.id, Messages.get("CHAINLINK-002301.validation.next.attribute"), this.next));
            }
        }
    }

    public VisitorNode getExecution(final Transition transition) {
        final Map<String, VisitorNode> executions;
        switch (transition.type) {
            case CHILD:
                executions = _executions;
                break;
            case SIBLING:
            default:
                if (parent == null) {
                    throw new IllegalStateException(Messages.format("CHAINLINK-002000.validation.no.parent.node", this.elementName, this.id));
                }
                executions = parent._executions;
        }
        return executions.get(transition.to);
    }

    public void addTransition(final String element, final String to) {
        if (to == null) {
            return;
        }
        this.transitions.add(new Transition(Transition.Type.SIBLING, this.elementName, this.id, element, to));
    }

    public void addChildTransition(final String element, final String to) {
        if (to == null) {
            return;
        }
        this.transitions.add(new Transition(Transition.Type.CHILD, this.elementName, this.id, element, to));
    }

    public void addParentTransition(final String element, final String to) {
        this.parent.addTransition(element, to);
    }

    public void addProblem(final String problem) {
        this.failed = true;
        this.problems.add(problem);
    }

    public void addCycle(final Cycle problem) {
        this.failed = true;
        for (final Cycle cycle : cycles) {
            if (cycle.isSameAs(problem)) {
                return;
            }
        }
        this.cycles.add(problem);
    }

    public Map<String, VisitorNode> getIds() {
        return ids;
    }

    public Execution getValue() {
        return value;
    }

    VisitorNode addChild(final VisitorNode child) {
        if (child.id != null) {
            this._executions.put(child.id, child);
        }
        this.children.add(child);
        return child;
    }

    // Formatting

    protected void ws(final StringBuilder builder, final int depth, final String section, final String last) {
        ws(builder, depth, section, section, section, last);
    }

    protected void ws(final StringBuilder builder, final int depth, final String first, final String section, final String secondLast, final String last) {
        for (int i = 0; i < depth; i++) {
            builder.append(i == (depth - 1)
                    ? secondLast
                    : i == 0
                        ? first
                        : section
            );
        }
        builder.append(last);
    }

    protected StringBuilder element(final StringBuilder builder) {
        return element(this.elementName, this.id, builder);
    }

    public static StringBuilder element(final String element, final String id, final StringBuilder builder) {
        builder.append('<')
                .append(element);
        if (id != null) {
            builder.append(" id=\"")
                    .append(id)
                    .append('"');
        }
        return builder.append('>');
    }

    public final StringBuilder toTree(final StringBuilder builder) {
        return toTree(builder, 0);
    }

    protected StringBuilder toTree(final StringBuilder builder, final int depth) {
        toTreeInternal(builder, depth);
        toTreeChildren(builder, depth);
        return builder;
    }

    protected StringBuilder toTreeChildren(final StringBuilder builder, final int depth) {
        for (final VisitorNode context : this.children) {
            context.toTree(builder, depth + 1);
        }
        return builder;
    }

    protected StringBuilder toTreeInternal(final StringBuilder builder, final int depth) {
        if (this.failed) {
            builder.append(ERROR);
            ws(builder, depth, "---", "---", "-> ", "+- ");
            element(builder).append(System.lineSeparator());
            for (final String problem : this.problems) {
                builder.append(PLAIN);
                ws(builder, depth, "|  ", "|     {  ");
                builder.append(problem).append(System.lineSeparator());
            }
            for (final Cycle cycle : this.cycles) {
                builder.append(PLAIN);
                ws(builder, depth, "|  ", "|     {  ");
                builder.append(Messages.get("CHAINLINK-002106.validation.cycle.detected")).append(System.lineSeparator());
                for (final Pair<Transition, VisitorNode> entry : cycle) {
                    builder.append(PLAIN);
                    ws(builder, depth, "|  ", "|     {  ");
                    final Transition transition = entry.getName();
                    final VisitorNode visitor = entry.getValue();
                    builder.append("  ")
                            .append(visitor.id)
                            .append(" -> ")
                            .append(transition.to)
                            .append(" ");
                    visitor.element(builder)
                            .append(" (")
                            .append(transition.toElement)
                            .append(")  ");
                    builder.append(System.lineSeparator());
                }
            }
        } else {
            builder.append(PLAIN);
            ws(builder, depth, "|  ", "+- ");
            element(builder).append(System.lineSeparator());
        }
        return builder;
    }

    public static class Cycle extends LinkedList<Pair<Transition, VisitorNode>> {
        private static final long serialVersionUID = 1L;

        public Cycle() {
        }

        public Cycle(final Collection<? extends Pair<Transition, VisitorNode>> c) {
            super(c);
        }

        boolean isSameAs(final Cycle cycle) {
            if (cycle.size() != size()) {
                return false;
            }
            for (final Pair<Transition, VisitorNode> theirs : cycle) {
                if (!contains(theirs)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public Cycle subList(final int fromIndex, final int toIndex) {
            return new Cycle(super.subList(fromIndex, toIndex));
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof VisitorNode)) return false;

        final VisitorNode that = (VisitorNode) o;

        if (failed != that.failed) return false;
        if (!children.equals(that.children)) return false;
        if (element != null ? !element.equals(that.element) : that.element != null) return false;
        if (elementName != null ? !elementName.equals(that.elementName) : that.elementName != null) return false;
        if (!_executions.equals(that._executions)) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (ids != null ? !ids.equals(that.ids) : that.ids != null) return false;
        if (next != null ? !next.equals(that.next) : that.next != null) return false;
        if (parent != null ? !parent.equals(that.parent) : that.parent != null) return false;
        if (!problems.equals(that.problems)) return false;
        if (!transitions.equals(that.transitions)) return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ids != null ? ids.hashCode() : 0;
        result = 31 * result + problems.hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + children.hashCode();
        result = 31 * result + (elementName != null ? elementName.hashCode() : 0);
        result = 31 * result + (element != null ? element.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (failed ? 1 : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (next != null ? next.hashCode() : 0);
        result = 31 * result + _executions.hashCode();
        result = 31 * result + transitions.hashCode();
        return result;
    }
}
