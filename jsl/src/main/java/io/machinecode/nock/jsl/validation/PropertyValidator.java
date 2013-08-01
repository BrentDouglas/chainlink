package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.api.Property;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyValidator extends Validator<Property> {

    public static final PropertyValidator INSTANCE = new PropertyValidator();

    protected PropertyValidator() {
        super("property");
    }

    @Override
    public void doValidate(final Property that, final ValidationContext context) {
        if (that.getName() == null) {
            context.addProblem("Attribute 'name' is required");
        }
        if (that.getValue() == null) {
            context.addProblem("Attribute 'value' is required");
        }
    }
}
