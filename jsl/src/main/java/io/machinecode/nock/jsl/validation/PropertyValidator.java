package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.spi.element.Property;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertyValidator extends Validator<Property> {

    public static final PropertyValidator INSTANCE = new PropertyValidator();

    protected PropertyValidator() {
        super(Property.ELEMENT);
    }

    @Override
    public void doValidate(final Property that, final ValidationContext context) {
        if (that.getName() == null) {
            context.addProblem(Problem.attributeRequired("name"));
        }
        if (that.getValue() == null) {
            context.addProblem(Problem.attributeRequired("value"));
        }
    }
}
