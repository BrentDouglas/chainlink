package io.machinecode.nock.jsl.validation.chunk;

import io.machinecode.nock.jsl.api.chunk.ExceptionClass;
import io.machinecode.nock.jsl.api.chunk.ExceptionClassFilter;
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
                    context.addProblem("Must not have null 'includes' element.");
                    continue;
                }
                if (clazz.getClassName() == null) {
                    context.addProblem("Attribute 'class' must not be null.");
                }
            }
        }
        if (that.getExcludes() != null) {
            for (final ExceptionClass clazz : that.getExcludes()) {
                if (clazz == null) {
                    context.addProblem("Must not have null 'excludes' element.");
                    continue;
                }
                if (clazz.getClassName() == null) {
                    context.addProblem("Attribute 'class' must not be null.");
                }
            }
        }
    }
}
