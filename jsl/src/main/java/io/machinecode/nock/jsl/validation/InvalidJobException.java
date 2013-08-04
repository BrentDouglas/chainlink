package io.machinecode.nock.jsl.validation;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class InvalidJobException extends RuntimeException {

    private final ValidationContext context;

    public InvalidJobException(final ValidationContext context) {
        this.context = context;
    }

    @Override
    public String getMessage() {
        return context.toTree(new StringBuilder(System.lineSeparator())).toString();
    }
}
