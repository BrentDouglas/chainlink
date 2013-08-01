package io.machinecode.nock.jsl.validation;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class CycleException extends RuntimeException {

    private final CycleContext context;

    public CycleException(final CycleContext context) {
        this.context = context;
    }

    @Override
    public String getMessage() {
        return context.toTree(new StringBuilder(System.lineSeparator())).toString();
    }
}
