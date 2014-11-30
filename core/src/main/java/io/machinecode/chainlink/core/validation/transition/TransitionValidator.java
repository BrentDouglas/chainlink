package io.machinecode.chainlink.core.validation.transition;

import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.transition.End;
import io.machinecode.chainlink.spi.element.transition.Fail;
import io.machinecode.chainlink.spi.element.transition.Next;
import io.machinecode.chainlink.spi.element.transition.Stop;
import io.machinecode.chainlink.spi.element.transition.Transition;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
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
