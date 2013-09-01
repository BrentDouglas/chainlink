package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.transition.Next;
import io.machinecode.nock.spi.util.Message;

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
            context.addProblem(Message.attributeRequired("on"));
        }
        if (that.getTo() == null) {
            context.addProblem(Message.attributeRequired("to"));
        } else {
            context.setTransition(null, that.getTo());
        }
    }
}
