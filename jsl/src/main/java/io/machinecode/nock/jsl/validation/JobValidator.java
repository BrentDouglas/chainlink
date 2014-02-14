package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.validation.execution.ExcecutionValidator;
import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.element.execution.Decision;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.util.Messages;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobValidator extends ValidatingVisitor<Job> {

    public static final JobValidator INSTANCE = new JobValidator();

    protected JobValidator() {
        super(Job.ELEMENT);
    }

    @Override
    public void doVisit(final Job that, final VisitorNode node) {
        if (that.getId() == null) {
            node.addProblem(Messages.format("NOCK-002102.validation.required.attribute", "id"));
        }
        if (!"1.0".equals(that.getVersion())) {
            node.addProblem(Messages.format("NOCK-002104.validation.matches.attribute", "version", that.getVersion(), "1.0"));
        }

        if (that.getListeners() != null) {
            ListenersValidator.INSTANCE.visit(that.getListeners(), node);
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.visit(that.getProperties(), node);
        }

        if (that.getExecutions() == null || that.getExecutions().isEmpty()) {
            node.addProblem(Messages.get("NOCK-002108.validation.executions.required"));
        } else {
            if (that.getExecutions().get(0) instanceof Decision) {
                node.addProblem(Messages.format("NOCK-002101.validation.decision.first.execution"));
            }
            for (final Execution execution : that.getExecutions()) {
                if (execution == null) {
                    node.addProblem(Messages.format("NOCK-002100.validation.not.null.element", "execution"));
                    continue;
                }
                ExcecutionValidator.visit(execution, node);
            }
        }
    }
}
