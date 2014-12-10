package io.machinecode.chainlink.core.validation.transition;

import io.machinecode.chainlink.core.validation.visitor.ValidatingVisitor;
import io.machinecode.chainlink.core.validation.visitor.VisitorNode;
import io.machinecode.chainlink.spi.element.transition.Next;
import io.machinecode.chainlink.spi.util.Messages;

/**
 * @author <a href="mailto:brent.n.douglas@gmail.com">Brent Douglas</a>
 * @since 1.0
 */
public class NextValidator extends ValidatingVisitor<Next> {

    public static final NextValidator INSTANCE = new NextValidator();

    protected NextValidator() {
        super(Next.ELEMENT);
    }

    @Override
    public void doVisit(final Next that, final VisitorNode context) {
        if (that.getOn() == null) {
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "on"));
        }
        if (that.getTo() == null) {
            context.addProblem(Messages.format("CHAINLINK-002102.validation.required.attribute", "to"));
        } else {
            context.addParentTransition(Messages.get("CHAINLINK-002300.validation.next.transition"), that.getTo());
        }
    }
}
