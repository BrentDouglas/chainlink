package io.machinecode.nock.jsl.visitor;

import io.machinecode.nock.spi.work.ExecutionWork;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class Visitor<T> {

    private final String element;

    protected Visitor(final String element) {
        this.element = element;
    }

    public final VisitorNode visit(T that) {
        final VisitorNode root = new VisitorNode(element);
        doVisit(that, root);
        return root;
    }

    /**
     * Needs to be called internally
     * @param that
     * @param parent
     */
    public void visit(T that, final VisitorNode parent) {
        final VisitorNode child;
        if (that instanceof ExecutionWork) { //TODO
            child = new VisitorNode(element, (ExecutionWork)that, parent);
        } else {
            child = new VisitorNode(element, parent);
        }
        parent.addChild(child);
        doVisit(that, child);
    }

    protected abstract void doVisit(T that, final VisitorNode node);
}
