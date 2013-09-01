package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.visitor.VisitorNode;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InvalidJobException extends RuntimeException {

    private final VisitorNode node;

    public InvalidJobException(final VisitorNode node) {
        this.node = node;
    }

    @Override
    public String getMessage() {
        return node.toTree(new StringBuilder(System.lineSeparator())).toString();
    }
}
