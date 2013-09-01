package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.validation.execution.ExcecutionValidator;
import io.machinecode.nock.jsl.visitor.ValidatingVisitor;
import io.machinecode.nock.jsl.visitor.VisitorNode;
import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.spi.util.Message;

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
            node.addProblem(Message.attributeRequired("id"));
        } else {
            node.setTransition(that.getId(), null);
        }
        if (!"1.0".equals(that.getVersion())) {
            node.addProblem(Message.attributeMatches("version", that.getVersion(), "1.0"));
        }

        if (that.getListeners() != null) {
            ListenersValidator.INSTANCE.visit(that.getListeners(), node);
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.visit(that.getProperties(), node);
        }

        if (that.getExecutions() == null || that.getExecutions().isEmpty()) {
            node.addProblem(Message.executionsRequired());
        } else {
            for (final Execution execution : that.getExecutions()) {
                if (execution == null) {
                    node.addProblem(Message.notNullElement("execution"));
                    continue;
                }
                ExcecutionValidator.visit(execution, node);
            }
        }
    }
}
