package io.machinecode.nock.jsl.validation;

import io.machinecode.nock.jsl.api.Job;
import io.machinecode.nock.jsl.api.execution.Execution;
import io.machinecode.nock.jsl.validation.execution.ExcecutionValidator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class JobValidator extends Validator<Job> {

    public static final JobValidator INSTANCE = new JobValidator();

    protected JobValidator() {
        super("job");
    }

    @Override
    public void doValidate(final Job that, final ValidationContext context) {
        if (that.getId() == null) {
            context.addProblem("Attribute 'id' is required.");
        } else {
            context.addId(that.getId());
        }
        if (that.getListeners() != null) {
            ListenersValidator.INSTANCE.validate(that.getListeners(), context);
        }
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.validate(that.getProperties(), context);
        }
        for (final Execution execution : that.getExecutions()) {
            if (execution == null) {
                context.addProblem("Must not have null 'execution' element.");
                continue;
            }
            ExcecutionValidator.validate(execution, context);
        }

        if (!"1.0".equals(that.getVersion())) {
            context.addProblem("Attribute 'version' must match '1.0'.");
        }
    }
}
