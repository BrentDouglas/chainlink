package io.machinecode.nock.jsl.validation.task;

import io.machinecode.nock.spi.element.task.ExceptionClass;
import io.machinecode.nock.spi.element.task.ExceptionClassFilter;
import io.machinecode.nock.jsl.validation.Problem;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ExecutionClassFilterValidator extends Validator<ExceptionClassFilter> {

    public static final ExecutionClassFilterValidator SKIPPABLE = new ExecutionClassFilterValidator("skippable-exception-classes");
    public static final ExecutionClassFilterValidator RETRYABLE = new ExecutionClassFilterValidator("retryable-exception-classes");
    public static final ExecutionClassFilterValidator NO_ROLLBACK = new ExecutionClassFilterValidator("no-rollback-exception-classes");

    protected ExecutionClassFilterValidator(final String element) {
        super(element);
    }

    @Override
    public void doValidate(final ExceptionClassFilter that, final ValidationContext context) {
        if (that.getIncludes() != null) {
            for (final ExceptionClass clazz : that.getIncludes()) {
                if (clazz == null) {
                    context.addProblem(Problem.notNullElement("includes"));
                    continue;
                }
                if (clazz.getClassName() == null) {
                    context.addProblem(Problem.attributeRequired("class"));
                }
            }
        }
        if (that.getExcludes() != null) {
            for (final ExceptionClass clazz : that.getExcludes()) {
                if (clazz == null) {
                    context.addProblem(Problem.notNullElement("excludes"));
                    continue;
                }
                if (clazz.getClassName() == null) {
                    context.addProblem(Problem.attributeRequired("class"));
                }
            }
        }
    }
}
