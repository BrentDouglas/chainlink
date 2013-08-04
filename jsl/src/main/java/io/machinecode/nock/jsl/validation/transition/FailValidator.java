package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.api.transition.Fail;
import io.machinecode.nock.jsl.validation.Problem;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class FailValidator extends Validator<Fail> {

    public static final FailValidator INSTANCE = new FailValidator();

    protected FailValidator() {
        super(Fail.ELEMENT);
    }

    @Override
    public void doValidate(final Fail that, final ValidationContext context) {
        if (that.getOn() == null) {
            context.addProblem(Problem.attributeRequired("on"));
        }
    }
}
