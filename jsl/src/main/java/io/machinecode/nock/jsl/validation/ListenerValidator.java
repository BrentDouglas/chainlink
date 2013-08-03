package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.api.Listener;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ListenerValidator extends Validator<Listener> {

    public static final ListenerValidator INSTANCE = new ListenerValidator();

    protected ListenerValidator() {
        super("listener");
    }

    @Override
    public void doValidate(final Listener that, final ValidationContext context) {
        if (that.getRef() == null) {
            context.addProblem(Problem.attributeRequired("ref"));
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.validate(that.getProperties(), context);
        }
    }
}
