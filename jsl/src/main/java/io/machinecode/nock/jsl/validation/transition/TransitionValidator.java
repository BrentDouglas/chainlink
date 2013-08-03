package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.api.transition.End;
import io.machinecode.nock.jsl.api.transition.Fail;
import io.machinecode.nock.jsl.api.transition.Next;
import io.machinecode.nock.jsl.api.transition.Stop;
import io.machinecode.nock.jsl.api.transition.Transition;
import io.machinecode.nock.jsl.validation.ValidationContext;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class TransitionValidator {

    private TransitionValidator(){}

    public static void validate(final Transition that, final ValidationContext context) {
        if (that instanceof Stop) {
            StopValidator.INSTANCE.validate((Stop) that, context);
        } else if (that instanceof Fail) {
            FailValidator.INSTANCE.validate((Fail) that, context);
        } else if (that instanceof Next) {
            NextValidator.INSTANCE.validate((Next) that, context);
        } else if (that instanceof End) {
            EndValidator.INSTANCE.validate((End) that, context);
        }
    }
}
