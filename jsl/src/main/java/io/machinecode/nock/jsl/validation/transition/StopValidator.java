package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.api.transition.Stop;
import io.machinecode.nock.jsl.validation.Problem;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class StopValidator extends Validator<Stop> {

    public static final StopValidator INSTANCE = new StopValidator();

    protected StopValidator() {
        super("stop");
    }

    @Override
    public void doValidate(final Stop that, final ValidationContext context) {
        if (that.getOn() == null) {
            context.addProblem(Problem.attributeRequired("on"));
        }
    }
}
