package io.machinecode.chainlink.core.validation.visitor;

import gnu.trove.set.hash.THashSet;
import io.machinecode.chainlink.jsl.core.util.ImmutablePair;
import io.machinecode.chainlink.spi.element.Element;
import io.machinecode.chainlink.spi.util.Messages;

import java.util.Set;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class ValidatingVisitor<T extends Element> extends Visitor<T> {

    protected ValidatingVisitor(final String element) {
        super(element);
    }

    public static boolean findProblems(final VisitorNode node) {
        boolean ret = false;
        //Root node is a job
        for (final VisitorNode execution : node.children) {
            final VisitorNode.Cycle cycle = new VisitorNode.Cycle();
            final Set<String> trail = new THashSet<String>();
            ret = _findProblems(execution, cycle, trail) || ret;
        }
        return ret;
    }

    private static boolean _findProblems(final VisitorNode node, final VisitorNode.Cycle cycle, final Set<String> trail) {
        boolean ret = false;
        if (node.id != null) {
            if (!trail.add(node.id)) {
                for (int i = cycle.size() - 1; i >= 0; --i) {
                    final VisitorNode that = cycle.get(i).getValue();
                    if (that.equals(node)) {
                        that.addCycle(cycle.subList(i, cycle.size()));
                        ret = true;
                        break;
                    }
                }
                if (!ret) {
                    cycle.get(0).getValue().addCycle(cycle);
                    ret = true;
                }
            } else {
                for (final Transition transition : node.transitions) {
                    final VisitorNode execution = node.getExecution(transition);
                    if (execution == null) {
                        node.addProblem(Messages.format("CHAINLINK-002107.validation.invalid.transition", node.id, transition.to));
                        ret = true;
                        continue;
                    }
                    final VisitorNode.Cycle subCycle = new VisitorNode.Cycle(cycle);
                    final THashSet<String> subTrail = new THashSet<String>(trail);
                    subCycle.add(ImmutablePair.of(transition, node));
                    ret = _findProblems(
                            execution,
                            subCycle,
                            subTrail
                    ) || ret;
                }
            }
        }
        for (final VisitorNode execution : node.children) {
            ret = _findProblems(execution, new VisitorNode.Cycle(), new THashSet<String>()) || ret;
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