package io.machinecode.nock.jsl.visitor;

import gnu.trove.set.hash.THashSet;
import io.machinecode.nock.spi.util.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class ValidatingVisitor<T> extends Visitor<T> {

    protected ValidatingVisitor(final String element) {
        super(element);
    }

    public static boolean hasCycle(final VisitorNode node) {
        final List<VisitorNode> cycle = new LinkedList<VisitorNode>();
        final Set<String> trail = new THashSet<String>();
        boolean ret = false;
        //Root node
        if (node.id != null) {
            VisitorNode that = node;
            cycle.add(that);
            trail.add(that.id);
            while ((that = node.jobScope.get(that.next)) != null) {
                cycle.add(that);
                if (!trail.add(that.id)) {
                    node.addProblem(Message.cycleDetected());
                    for (final VisitorNode problem : cycle) {
                        final StringBuilder builder = new StringBuilder()
                                .append("  ")
                                .append(problem.id)
                                .append(" -> ")
                                .append(problem.next);
                        node.addProblem(problem.element(builder).toString());
                    }
                    final StringBuilder builder = new StringBuilder()
                            .append("  ")
                            .append(that.id)
                            .append(" -> ")
                            .append(that.next);
                    node.addProblem(that.element(builder).toString());
                    ret = true;
                    break;
                }
            }
        }

        for (final VisitorNode child : node.children) {
            ret = hasCycle(child) || ret;
        }

        return ret;
    }

    public static boolean hasInvalidTransfer(final VisitorNode node) {
        boolean ret = false;

        //Only report this for the parent context
        if (node.parent == null || node.localScope != node.parent.localScope) {
            for (final Transition entry : node.transitions) {
                if (!node.localScope.containsKey(entry.next)) {
                    node.addProblem(Message.invalidTransition(VisitorNode.element(entry.element, entry.id, entry.next, new StringBuilder()).toString()));
                    ret = true;
                }
            }
        }

        for (final VisitorNode child : node.children) {
            ret = hasInvalidTransfer(child) || ret;
        }

        return ret;
    }


    public static boolean hasFailed(final VisitorNode node) {
        if (node.failed) {
            return true;
        }
        for (final VisitorNode child : node.children) {
            if (hasFailed(child)) {
                return true;
            }
        }
        return false;
    }
}