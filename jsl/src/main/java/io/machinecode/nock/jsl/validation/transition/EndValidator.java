package io.machinecode.nock.jsl.validation.transition;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.transition.End;
import io.machinecode.nock.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class EndValidator extends ValidatingVisitor<End> {

    public static final EndValidator INSTANCE = new EndValidator();

    protected EndValidator() {
        super(End.ELEMENT);
    }

    @Override
    public void doVisit(final End that, final VisitorNode context) {
        if (that.getOn() == null) {
            context.addProblem(Messages.format("validation.required.attribute", "on"));
        }
    }
}
