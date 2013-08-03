package io.machinecode.nock.jsl.validation;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class Validator<T> {

    private final String element;

    protected Validator(final String element) {
        this.element = element;
    }

    public final void validate(T that) {
        final ValidationContext context = new ValidationContext(element);
        doValidate(that, context);
        if (context.hasFailed()) {
            throw new InvalidJobDefinitionException(context);
        }
    }

    /**
     * Needs tp be called internally
     * @param that
     * @param parent
     */
    public void validate(T that, final ValidationContext parent) {
        final ValidationContext child = new ValidationContext(element, parent);
        parent.addChild(child);
        doValidate(that, child);
    }

    protected abstract void doValidate(T that, final ValidationContext context);

}
