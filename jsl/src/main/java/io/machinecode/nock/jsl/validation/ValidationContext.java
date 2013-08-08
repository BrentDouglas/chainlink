package io.machinecode.nock.jsl.validation;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ValidationContext<T extends ValidationContext<T>> {
    public static final String ERROR = "FAILURE -";
    public static final String PLAIN = "         ";

    protected final Map<String, ValidationContext> ids;
    protected final List<String> problems = new ArrayList<String>(0);
    protected final List<T> children = new ArrayList<T>(0);
    protected final String element;
    protected boolean failed = false;

    public ValidationContext(final String element) {
        this.element = element;
        this.ids = new THashMap<String, ValidationContext>(0);
    }

    public ValidationContext(final String element, final T parent) {
        this.element = element;
        this.ids = parent.ids;
    }

    public final StringBuilder toTree(final StringBuilder builder) {
        return toTree(builder, 0);
    }

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

    protected StringBuilder toTree(final StringBuilder builder, final int depth) {
        toTreeInternal(builder, depth);
        toTreeChildren(builder, depth);
        return builder;
    }

    protected StringBuilder toTreeInternal(final StringBuilder builder, final int depth) {
        if (failed) {
            builder.append(ERROR);
            ws(builder, depth, "---", "---", "-> ", "+- ");
            element(builder)
                    .append(System.lineSeparator());
            for (int i = 0; i < problems.size(); ++i) {
                final String problem = problems.get(i);
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

    protected StringBuilder element(final StringBuilder builder) {
        return  builder.append('<')
                    .append(element)
                .append('>');
    }

    protected StringBuilder toTreeChildren(final StringBuilder builder, final int depth) {
        for (final ValidationContext<?> context : children) {
            context.toTree(builder, depth + 1);
        }
        return builder;
    }

    public void addProblem(final String problem) {
        this.problems.add(problem);
        failed = true;
    }

    public void addId(final String id) {
        final ValidationContext<?> old;
        if ((old = this.ids.put(id, this)) != null) {
            addProblem(Problem.nonUniqueId(id));
            old.addProblem(Problem.nonUniqueId(id));
        }
    }

    T addChild(final T child) {
        this.children.add(child);
        return child;
    }

    boolean hasFailed() {
        if (failed) {
            return true;
        }
        for (final ValidationContext<?> context : children) {
            if (context.hasFailed()) {
                return true;
            }
        }
        return false;
    }
}
