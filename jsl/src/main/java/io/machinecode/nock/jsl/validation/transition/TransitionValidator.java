package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.transition.End;
import io.machinecode.nock.spi.element.transition.Fail;
import io.machinecode.nock.spi.element.transition.Next;
import io.machinecode.nock.spi.element.transition.Stop;
import io.machinecode.nock.spi.element.transition.Transition;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public final class TransitionValidator {

    private TransitionValidator(){}

    public static void visit(final Transition that, final VisitorNode context) {
        if (that instanceof Stop) {
            StopValidator.INSTANCE.visit((Stop) that, context);
        } else if (that instanceof Fail) {
            FailValidator.INSTANCE.visit((Fail) that, context);
        } else if (that instanceof Next) {
            NextValidator.INSTANCE.visit((Next) that, context);
        } else if (that instanceof End) {
            EndValidator.INSTANCE.visit((End) that, context);
        }
    }
}
