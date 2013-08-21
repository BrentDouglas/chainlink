package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.spi.element.Job;
import io.machinecode.nock.spi.element.execution.Execution;
import io.machinecode.nock.jsl.validation.execution.ExcecutionValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobValidator extends Validator<Job> {

    public static final JobValidator INSTANCE = new JobValidator();

    protected JobValidator() {
        super(Job.ELEMENT);
    }

    @Override
    public void doValidate(final Job that, final ValidationContext context) {
        if (that.getId() == null) {
            context.addProblem(Problem.attributeRequired("id"));
        } else {
            context.addId(that.getId());
        }
        if (!"1.0".equals(that.getVersion())) {
            context.addProblem(Problem.attributeMatches("version", that.getVersion(), "1.0"));
        }

        if (that.getListeners() != null) {
            ListenersValidator.INSTANCE.validate(that.getListeners(), context);
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.validate(that.getProperties(), context);
        }

        if (that.getExecutions() == null | that.getExecutions().isEmpty()) {
            context.addProblem(Problem.executionsRequired());
        } else {
            for (final Execution execution : that.getExecutions()) {
                if (execution == null) {
                    context.addProblem(Problem.notNullElement("execution"));
                    continue;
                }
                ExcecutionValidator.validate(execution, context);
            }
        }
    }
}
