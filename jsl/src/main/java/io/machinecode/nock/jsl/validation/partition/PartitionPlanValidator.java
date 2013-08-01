package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.jsl.api.partition.PartitionPlan;
import io.machinecode.nock.jsl.validation.PropertiesValidator;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PartitionPlanValidator extends Validator<PartitionPlan> {

    public static final PartitionPlanValidator INSTANCE = new PartitionPlanValidator();

    protected PartitionPlanValidator() {
        super("plan");
    }

    @Override
    public void doValidate(final PartitionPlan that, final ValidationContext context) {
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.validate(that.getProperties(), context);
        }
        if (that.getPartitions() < 0) {
            context.addProblem("Attribute 'partitions' must be positive. Found '" + that.getPartitions() + "'.");
        }
        if (that.getThreads() == null) {
            context.addProblem("Attribute 'threads' must not be null");
        } else if (that.getThreads() < 0) {
            context.addProblem("Attribute 'threads' must be positive. Found '" + that.getThreads() + "'.");
        }
    }
}
