package io.machinecode.nock.jsl.validation;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InvalidTransitionException extends RuntimeException {

    private final TransitionContext context;

    public InvalidTransitionException(final TransitionContext context) {
        this.context = context;
    }

    @Override
    public String getMessage() {
        return context.toTree(new StringBuilder(System.lineSeparator())).toString();
    }
}
