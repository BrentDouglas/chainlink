package io.machinecode.chainlink.core.validation;

import io.machinecode.chainlink.core.validation.visitor.VisitorNode;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class InvalidJobException extends RuntimeException {

    public InvalidJobException(final VisitorNode node) {
        super(node.toTree(new StringBuilder(System.lineSeparator())).toString());
    }
}
