package io.machinecode.nock.jsl.validation.execution;

import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.execution.Flow;
import io.machinecode.nock.spi.element.execution.Split;
import io.machinecode.nock.spi.util.Messages;

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
            context.addProblem(Messages.format("NOCK-002102.validation.required.attribute", "id"));
        } else {
            context.addTransition(Messages.get("NOCK-002301.validation.next.attribute"), that.getNext());
        }

        if (that.getFlows() != null) {
            for (final Flow flow : that.getFlows()) {
                if (flow == null) {
                    context.addProblem(Messages.format("NOCK-002100.validation.not.null.element", "flow"));
                    continue;
                }
                context.addChildTransition(Messages.get("NOCK-002303.validation.split.implicit"), flow.getId());
                FlowValidator.INSTANCE.visit(flow, context);
            }
        }
    }
}
