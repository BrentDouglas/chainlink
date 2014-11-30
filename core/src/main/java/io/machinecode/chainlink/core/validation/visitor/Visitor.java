package io.machinecode.chainlink.core.validation.visitor;

import io.machinecode.chainlink.spi.element.Element;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 */
public abstract class Visitor<T extends Element> {

    private final String element;

    protected Visitor(final String element) {
        this.element = element;
    }

    public final VisitorNode visit(T that) {
        final VisitorNode root = new VisitorNode(element, that);
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
        child = new VisitorNode(element, that, parent);
        parent.addChild(child);
        doVisit(that, child);
    }

    protected abstract void doVisit(T that, final VisitorNode node);
}
