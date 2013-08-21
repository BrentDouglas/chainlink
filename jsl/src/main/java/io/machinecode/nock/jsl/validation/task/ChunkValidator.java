package io.machinecode.nock.jsl.validation.task;

import io.machinecode.nock.spi.element.task.Chunk;
import io.machinecode.nock.spi.element.task.Chunk.CheckpointPolicy;
import io.machinecode.nock.jsl.validation.Problem;
import io.machinecode.nock.jsl.validation.ValidationContext;
import io.machinecode.nock.jsl.validation.Validator;

/**
 * @author Brent Douglas <brent.n.douglas@gmail.com>
 */
public class ChunkValidator extends Validator<Chunk> {

    public static final ChunkValidator INSTANCE = new ChunkValidator();

    protected ChunkValidator() {
        super(Chunk.ELEMENT);
    }

    @Override
    public void doValidate(final Chunk that, final ValidationContext context) {
        if (that.getCheckpointPolicy() == null) {
            context.addProblem(Problem.attributeRequired("checkpoint-policy"));
        }
        if (!CheckpointPolicy.ITEM.equals(that.getCheckpointPolicy())
                && !CheckpointPolicy.CUSTOM.equals(that.getCheckpointPolicy())) {
            context.addProblem(Problem.attributeMatches("checkpoint-policy", that.getCheckpointPolicy(), CheckpointPolicy.ITEM, CheckpointPolicy.CUSTOM));
        }

        //if (that.getItemCount() < 0) {
        //    context.addProblem(Problem.attributePositive("item-count", that.getItemCount()));
        //}
        //if (that.getTimeLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("time-limit", that.getTimeLimit()));
        //}
        //if (that.getSkipLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("skip-limit", that.getSkipLimit()));
        //}
        //if (that.getRetryLimit() < 0) {
        //    context.addProblem(Problem.attributePositive("retry-limit", that.getRetryLimit()));
        //}

        if (that.getReader() == null) {
            context.addProblem(Problem.notNullElement("reader"));
        }
        if (that.getProcessor() == null) {
            context.addProblem(Problem.notNullElement("processor"));
        }
        if (that.getWriter() == null) {
            context.addProblem(Problem.notNullElement("writer"));
        }

        //Nullable

        if (that.getCheckpointAlgorithm() != null) {
            CheckpointAlgorithmValidator.INSTANCE.validate(that.getCheckpointAlgorithm(), context);
        }
        if (that.getNoRollbackExceptionClasses() != null) {
            ExecutionClassFilterValidator.NO_ROLLBACK.validate(that.getNoRollbackExceptionClasses(), context);
        }
        if (that.getSkippableExceptionClasses() != null) {
            ExecutionClassFilterValidator.SKIPPABLE.validate(that.getSkippableExceptionClasses(), context);
        }
        if (that.getRetryableExceptionClasses() != null) {
            ExecutionClassFilterValidator.RETRYABLE.validate(that.getRetryableExceptionClasses(), context);
        }
    }
}
