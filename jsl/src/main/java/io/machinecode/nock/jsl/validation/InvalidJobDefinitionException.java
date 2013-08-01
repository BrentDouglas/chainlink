package io.machinecode.nock.jsl.validation;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InvalidJobDefinitionException extends RuntimeException {

    private final ValidationContext context;

    public InvalidJobDefinitionException(final ValidationContext context) {
        this.context = context;
    }

    @Override
    public String getMessage() {
        return context.toTree(new StringBuilder(System.lineSeparator())).toString();
    }
}
