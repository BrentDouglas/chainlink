package io.machinecode.nock.jsl.validation;

import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
* @author Brent Douglas <brent.n.douglas@gmail.com>
*/
public class ValidationContext {
    public static final String ERROR = "FAIL  ";
    public static final String PLAIN = "      ";

    private final Set<String> ids;
    private final List<String> problems = new ArrayList<String>(0);
    private final List<ValidationContext> children = new ArrayList<ValidationContext>(0);
    private final String element;
    private boolean failed = false;

    public ValidationContext(final String element) {
        this.element = element;
        this.ids = new THashSet<String>(0);
    }

    public ValidationContext(final String element, final ValidationContext parent) {
        this.element = element;
        this.ids = parent.ids;
    }

    public final StringBuilder toTree(final StringBuilder builder) {
        return toTree(builder, 0);
    }

    private void ws(final StringBuilder builder, final int depth, final String first, final String section, final String last) {
        builder.append(first);
        for (int i = 0; i < depth; i++) {
            builder.append(section);
        }
        builder.append(last);
    }

    private StringBuilder toTree(final StringBuilder builder, final int depth) {
        if (failed) {
            builder.append(ERROR);
            ws(builder, depth, "+--", "---", "+> ");
            builder.append(element)
                    .append(System.lineSeparator());
            for (int i = 0; i < problems.size(); ++i) {
                final String problem = problems.get(i);
                builder.append(PLAIN);
                if (i == 0) {
                    ws(builder, depth, "|  ", "|  ", "\\->   ");
                } else if (i == problems.size() - 1) {
                    ws(builder, depth, "|  ", "|  ", " \\->  ");
                } else {
                    ws(builder, depth, "|  ", "|  ", " |->  ");
                }
                ++i;
                builder.append(problem)
                        .append(System.lineSeparator());
            }
        } else {
            builder.append(PLAIN);
            ws(builder, depth, "|  ", "|  ", "+- ");
            builder.append(element)
                    .append(System.lineSeparator());
        }
        for (final ValidationContext context : children) {
            context.toTree(builder, depth + 1);
        }
        return builder;
    }

    public void addProblem(final String problem) {
        this.problems.add(problem);
        failed = true;
    }

    public void addId(final String id) {
        if (!this.ids.add(id)) {
            addProblem("id '" + id + "' defined multiple times.");
        }
    }

    void addChild(final ValidationContext child) {
        this.children.add(child);
    }

    boolean hasFailed() {
        if (failed) {
            return true;
        }
        for (final ValidationContext context : children) {
            if (context.hasFailed()) {
                return true;
            }
        }
        return false;
    }
}
