package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.api.PropertyReference;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public abstract class PropertyReferenceValidator<T extends PropertyReference> extends Validator<T> {

    protected PropertyReferenceValidator(final String element) {
        super(element);
    }

    @Override
    public void doValidate(final T that, final ValidationContext context) {
        if (that.getRef() == null) {
            context.addProblem("Attribute 'ref 'is required.");
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.validate(that.getProperties(), context);
        }
    }
}
