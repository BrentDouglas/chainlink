package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.transition.Next;
import io.machinecode.nock.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class NextValidator extends ValidatingVisitor<Next> {

    public static final NextValidator INSTANCE = new NextValidator();

    protected NextValidator() {
        super(Next.ELEMENT);
    }

    @Override
    public void doVisit(final Next that, final VisitorNode context) {
        if (that.getOn() == null) {
            context.addProblem(Messages.format("NOCK-002102.validation.required.attribute", "on"));
        }
        if (that.getTo() == null) {
            context.addProblem(Messages.format("NOCK-002102.validation.required.attribute", "to"));
        } else {
            context.addParentTransition(Messages.get("NOCK-002300.validation.next.transition"), that.getTo());
        }
    }
}
