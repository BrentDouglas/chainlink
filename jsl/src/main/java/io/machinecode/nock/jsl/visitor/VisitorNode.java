package io.machinecode.nock.jsl.visitor;

import gnu.trove.map.hash.THashMap;
import io.machinecode.nock.spi.element.Element;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.util.Messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class VisitorNode {
    public static final String ERROR = "FAILURE -";
    public static final String PLAIN = "         ";

    protected final Map<String, VisitorNode> ids;
    protected final List<String> problems = new ArrayList<String>(0);
    protected final VisitorNode parent;
    protected final List<VisitorNode> children = new ArrayList<VisitorNode>(0);
    protected final String elementName;
    protected final Element element;
    protected final Execution value;
    protected boolean failed = false;

    protected String id;
    protected String next;
    protected final Map<String, VisitorNode> jobScope;
    protected final Map<String, VisitorNode> localScope;
    final List<Transition> transitions;

    public VisitorNode(final String elementName, final Element value) {
        this.elementName = elementName;
        this.element = value;
        this.value = null;
        this.parent = null;
        this.ids = new THashMap<String, VisitorNode>(0);
        this.jobScope = new THashMap<String, VisitorNode>(0);
        this.localScope = this.jobScope;
        this.transitions = new ArrayList<Transition>(0);
    }

    public VisitorNode(final String elementName, final Element value, final VisitorNode parent) {
        boolean ex = value instanceof Execution;
        this.elementName = elementName;
        this.element = value;
        this.value = ex ? (Execution)value : null;
        this.parent = parent;
        this.ids = parent.ids;
        if (value instanceof Flow) {
            this.transitions = new ArrayList<Transition>(0);
            this.localScope = new THashMap<String, VisitorNode>(0);
        } else if (ex) {
            this.transitions = new ArrayList<Transition>(0);
            this.localScope = parent.localScope;
        } else {
            this.transitions = parent.transitions;
            this.localScope = parent.localScope;
        }
        this.jobScope = parent.jobScope;
    }

    public void setTransition(final String id, final String next) {
        this.id = id;
        this.next = next;
        if (next != null) {
            this.transitions.add(new Transition(this.elementName, id, next));
        }
        if (id != null) {
            if (this.parent != null) {
                this.parent.localScope.put(id, this);
            }
            this.jobScope.put(id, this);
            final VisitorNode old;
            if ((old = this.ids.put(id, this)) != null) {
                addProblem(Messages.nonUniqueId(id));
                old.addProblem(Messages.nonUniqueId(id));
            }
        }
    }

    public void addProblem(final String problem) {
        this.problems.add(problem);
        this.failed = true;
    }

    VisitorNode addChild(final VisitorNode child) {
        this.children.add(child);
        return child;
    }

    // Getters

    public Object getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public String getNext() {
        return next;
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
        return element(this.elementName, this.id, this.next, builder);
    }

    public static StringBuilder element(final String element, final String id, final String next, final StringBuilder builder) {
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
            element(builder)
                    .append(System.lineSeparator());
            for (int i = 0; i < this.problems.size(); ++i) {
                final String problem = this.problems.get(i);
                builder.append(PLAIN);
                if (i == 0) {
                    ws(builder, depth, "|  ", "|     {  ");
                } else {
                    ws(builder, depth, "|  ", "|     {  ");
                }
                ++i;
                builder.append(problem)
                        .append(System.lineSeparator());
            }
        } else {
            builder.append(PLAIN);
            ws(builder, depth, "|  ", "+- ");
            element(builder).append(System.lineSeparator());
        }
        return builder;
    }
}
