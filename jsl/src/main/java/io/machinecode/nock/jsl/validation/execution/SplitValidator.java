package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.element.execution.Split;
import io.machinecode.nock.spi.util.Message;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class SplitValidator extends ValidatingVisitor<Split> {

    public static final SplitValidator INSTANCE = new SplitValidator();

    protected SplitValidator() {
        super(Split.ELEMENT);
    }

    @Override
    public void doVisit(final Split that, final VisitorNode context) {
        if (that.getId() == null) {
            context.addProblem(Message.attributeRequired("id"));
        } else {
            context.setTransition(that.getId(), that.getNext());
        }

        if (that.getFlows() != null) {
            for (final Flow flow : that.getFlows()) {
                if (flow == null) {
                    context.addProblem(Message.notNullElement("flow"));
                }
                FlowValidator.INSTANCE.visit(flow, context);
            }
        }
    }
}
