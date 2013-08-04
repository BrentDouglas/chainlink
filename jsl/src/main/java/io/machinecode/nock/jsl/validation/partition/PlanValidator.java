package io.machinecode.nock.jsl.validation.partition;

import io.machinecode.nock.jsl.api.partition.Plan;
import io.machinecode.nock.jsl.validation.Problem;
import io.machinecode.nock.jsl.validation.PropertiesValidator;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class PlanValidator extends Validator<Plan> {

    public static final PlanValidator INSTANCE = new PlanValidator();

    protected PlanValidator() {
        super(Plan.ELEMENT);
    }

    @Override
    public void doValidate(final Plan that, final ValidationContext context) {
        if (that.getProperties() != null) {
            PropertiesValidator.INSTANCE.validate(that.getProperties(), context);
        }
        if (that.getPartitions() < 0) {
            context.addProblem(Problem.attributePositive("partitions", that.getPartitions()));
        }
        //This can be null and will get set in the PlanImpl constructor
        if (that.getThreads() != null && that.getThreads() < 0) {
            context.addProblem(Problem.attributePositive("threads", that.getThreads()));
        }
    }
}
