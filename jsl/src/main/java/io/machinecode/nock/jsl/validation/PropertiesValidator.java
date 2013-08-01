package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.api.Properties;
import io.machinecode.nock.jsl.api.Property;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PropertiesValidator extends Validator<Properties> {

    public static final PropertiesValidator INSTANCE = new PropertiesValidator();

    protected PropertiesValidator() {
        super("properties");
    }

    @Override
    public void doValidate(final Properties that, final ValidationContext context) {
        for(final Property property : that.getProperties()) {
            if (property == null) {
                context.addProblem("Must not have null 'property' must element.");
                continue;
            }
            PropertyValidator.INSTANCE.validate(property, context);
        }
    }
}
